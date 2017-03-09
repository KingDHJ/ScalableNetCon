package com.dhj.scalablenetcon.update;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;


import com.dhj.scalablenetcon.User;
import com.dhj.scalablenetcon.UserDao;
import com.dhj.scalablenetcon.db.BaseDaoFactory;
import com.dhj.scalablenetcon.db.fileutil.FileUtil;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.content.ContentValues.TAG;

/**
 * Created by duanhuangjun on 17/2/27.
 */

public class UpdateManager {
    private static final String INFO_FILE_DIV = "/";
    private List<User> userList ;
    private  File parentFile=new File(Environment.getExternalStorageDirectory(),"update");
    private  File bakFile=new File(parentFile,"backDb");
    public UpdateManager()
    {
        if(!parentFile.exists())
        {
            parentFile.mkdirs();
        }
        if(!bakFile.exists())
        {
            bakFile.mkdirs();
        }

    }
    public void checkThisVersionTable(Context context )  {
        UserDao userDao= BaseDaoFactory.getInstance().getDataHelper(UserDao.class,User.class);

        userList=userDao.query(new User());
        UpdateDbXml xml = readDbXml(context);

        String thisVersion = this.getVersionName(context);
        CreateVersion thisCreateVersion = analyseCreateVersion(xml, thisVersion);
        try
        {
            executeCreateVersion(thisCreateVersion,true);
        } catch (Exception e)
        {
        }

    }

    /**
     * 开始升级
     * @param context
     */
    public void startUpdateDb(Context context)
    {
        UpdateDbXml updateDbxml=readDbXml(context);
        if(getLocalVersionInfo())
        {
            //拿到当前版本
            String thisVersion=getVersionName(context);
            //拿到上一个版本
            String lastVersion=lastBackupVersion;
            UpdateStep updateStep=analyseUpdateStep(updateDbxml,lastVersion,thisVersion);

            if(updateStep==null)
            {
                return;
            }
            List<UpdateDb> updateDbs=updateStep.getUpdateDbs();
            CreateVersion createVersion=analyseCreateVersion(updateDbxml,thisVersion);

            try {
                //更新每个用户的数据库
                for (User user:userList)
                {
                    String logicDbDir=parentFile.getAbsolutePath()+"/update"+"/"+user.getUser_id()+"/logic.db";

                    String  logicCopy=bakFile.getAbsolutePath()+"/"+user.getUser_id()+"/logic.db";
                    FileUtil.CopySingleFile(logicDbDir,logicCopy);
                }
                //备份总数据库
                String user=parentFile.getAbsolutePath()+"/user.db";
                String user_bak=bakFile.getAbsolutePath()+"/user.db";
                FileUtil.CopySingleFile(user,user_bak);
                // 第二步:执行sql_before语句，删除以及备份相关旧表
                executeDb(updateDbs, -1);

                // 第三步:检查新表，创建新表
                executeCreateVersion(createVersion,false);


                Log.i(TAG, "第三步检查新表完成!");
                // 第四步:从备份表中恢复数据，恢复后删除备份表
                executeDb(updateDbs, 1);
            }catch (Exception e)
            {

            }
            // 第五步:升级成功，删除备份数据库
            if (userList != null && !userList.isEmpty())
            {
                for (User user:userList)
                {
                    String logicDbDir=parentFile.getAbsolutePath()+"/update"+"/"+user.getUser_id()+".db";
                    File file=new File(logicDbDir);
                    if(file.exists())
                    {
                        file.delete();
                    }

                }
            }
            File userFileBak=new File(bakFile.getAbsolutePath()+"user_bak.db");
            if(userFileBak.exists())
            {
                userFileBak.delete();
            }

            Log.i(TAG,"升级成功");
        }
    }

    /**
     * 根据建表脚本,核实一遍应该存在的表
     *
     * @param createVersion
     * @throws Exception
     */
    private void executeCreateVersion(CreateVersion createVersion,boolean isLogic) throws Exception {
        if (createVersion == null || createVersion.getCreateDbs() == null)
        {
            throw new Exception("createVersion or createDbs is null;");
        }

        for (CreateDb cd : createVersion.getCreateDbs())
        {
            if (cd == null || cd.getName() == null)
            {
                throw new Exception("db or dbName is null when createVersion;");
            }

            if (!"logic".equals(cd.getName()))
            {
                continue;
            }

            // 创建数据库表sql
            List<String> sqls = cd.getSqlCreates();

            SQLiteDatabase sqlitedb = null;
            try
            {
                // 逻辑层数据库要做多用户升级
                if (userList != null && !userList.isEmpty())
                {
                    // 多用户建新表
                    for (int i = 0; i < userList.size(); i++)
                    {
                        // 获取db
                        sqlitedb = getDb(cd, userList.get(i).getUser_id());
                        executeSql(sqlitedb, sqls);
                        sqlitedb.close();
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            } finally
            {
                // 关闭数据库
                if (sqlitedb != null)
                {
                    sqlitedb.close();
                }
            }
        }
    }


    /**
     * 执行针对db升级的sql集合
     *
     * @param updateDbs 数据库操作脚本集合
     * @param type      小于0为建表前，大于0为建表后
     * @throws Exception
     * @throws throws    [违例类型] [违例说明]
     * @see
     */
    private void executeDb(List<UpdateDb> updateDbs, int type) throws Exception {
        if (updateDbs == null)
        {
            throw new Exception("updateDbs is null;");
        }
        for (UpdateDb db : updateDbs)
        {
            if (db == null || db.getDbName() == null)
            {
                throw new Exception("db or dbName is null;");
            }

            List<String> sqls = null;
            //更改表
            if (type < 0)
            {
                sqls = db.getSqlBefores();
            } else if (type > 0)
            {
                sqls = db.getSqlAfters();
            }

            SQLiteDatabase sqlitedb = null;

            try
            {
                // 逻辑层数据库要做多用户升级
                if (userList != null && !userList.isEmpty())
                {
                    // 多用户表升级
                    for (int i = 0; i < userList.size(); i++)
                    {
                        sqlitedb = getDb(db, userList.get(i).getUser_id());

                        executeSql(sqlitedb, sqls);

                        sqlitedb.close();
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            } finally
            {
                if (null != sqlitedb)
                {
                    sqlitedb.close();
                }
            }
        }
    }

    /**
     * 执行sql语句
     *
     * @param sqlitedb SQLiteDatabase
     * @param sqls     sql语句集合
     * @throws Exception 异常
     * @throws throws    [违例类型] [违例说明]
     * @see
     */
    private void executeSql(SQLiteDatabase sqlitedb, List<String> sqls) throws Exception {
        // 检查参数
        if (sqls == null || sqls.size() == 0)
        {
            return;
        }

        // 事务
        sqlitedb.beginTransaction();

        for (String sql : sqls)
        {
            sql = sql.replaceAll("\r\n", " ");
            sql = sql.replaceAll("\n", " ");
            if (!"".equals(sql.trim()))
            {
                try
                {
                    // Logger.i(TAG, "执行sql：" + sql, false);
                    sqlitedb.execSQL(sql);
                } catch (SQLException e)
                {
                }
            }
        }

        sqlitedb.setTransactionSuccessful();
        sqlitedb.endTransaction();
    }


    /**
     * 新表插入数据
     *
     * @param xml
     * @param lastVersion 上个版本
     * @param thisVersion 当前版本
     * @return
     */
    private UpdateStep analyseUpdateStep(UpdateDbXml xml, String lastVersion, String thisVersion) {
        if (lastVersion == null || thisVersion == null)
        {
            return null;
        }

        // 更新脚本
        UpdateStep thisStep = null;
        if (xml == null)
        {
            return null;
        }
        List<UpdateStep> steps = xml.getUpdateSteps();
        if (steps == null || steps.size() == 0)
        {
            return null;
        }

        for (UpdateStep step : steps)
        {
            if (step.getVersionFrom() == null || step.getVersionTo() == null)
            {
            } else
            {
                // 升级来源以逗号分隔
                String[] lastVersionArray = step.getVersionFrom().split(",");

                if (lastVersionArray != null && lastVersionArray.length > 0)
                {
                    for (int i = 0; i < lastVersionArray.length; i++)
                    {
                        // 有一个配到update节点即升级数据
                        if (lastVersion.equalsIgnoreCase(lastVersionArray[i]) && step.getVersionTo().equalsIgnoreCase(thisVersion))
                        {
                            thisStep = step;

                            break;
                        }
                    }
                }
            }
        }

        return thisStep;
    }
    /**
     * 根据xml对象获取对应要修改的db文件
     *
     * @param db
     * @return
     */
    private SQLiteDatabase getDb(UpdateDb db, String userId) {
        return getDb(db.getDbName(), userId);
    }

    private SQLiteDatabase getDb(CreateDb db, String userId) {
        return getDb(db.getName(), userId);
    }

    /**
     * 创建数据库,获取数据库对应的SQLiteDatabase
     *
     * @param dbname
     * @return 设定文件
     * @throws throws [违例类型] [违例说明]sta
     * @see
     */
    private SQLiteDatabase getDb(String dbname, String userId) {
        String dbfilepath = null;
        SQLiteDatabase sqlitedb = null;
        File file=new File(parentFile,userId);
        if(!file.exists())
        {
            file.mkdirs();
        }
        if (dbname.equalsIgnoreCase("logic"))
        {
            dbfilepath = file.getAbsolutePath()+ "/logic.db";// logic对应的数据库路径

        }else if (dbname.equalsIgnoreCase("user"))
        {
            dbfilepath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/user.db";// service对应的数据库
        }

        if (dbfilepath != null)
        {
            File f = new File(dbfilepath);
            f.mkdirs();
            if (f.isDirectory())
            {
                f.delete();
            }
            sqlitedb = SQLiteDatabase.openOrCreateDatabase(dbfilepath, null);
        }

        return sqlitedb;
    }


    /**
     * 解析出对应版本的建表脚本
     *
     * @return
     */
    private CreateVersion analyseCreateVersion(UpdateDbXml xml, String version) {
        CreateVersion cv = null;
        if (xml == null || version == null)
        {
            return cv;
        }

        List<CreateVersion> createVersions = xml.getCreateVersions();
        if (createVersions != null)
        {
            for (CreateVersion item : createVersions)
            {
                // 如果表相同则要支持xml中逗号分隔
                String[] createVersion = item.getVersion().trim().split(",");

                for (int i = 0; i < createVersion.length; i++)
                {
                    if (createVersion[i].trim().equalsIgnoreCase(version))
                    {
                        cv = item;

                        break;
                    }
                }
            }
        }

        return cv;
    }

    /**
     * 读取升级xml
     *
     * @param context
     * @return
     */
    private UpdateDbXml readDbXml(Context context) {
        InputStream is = null;
        Document document = null;
        try
        {
            is = context.getAssets().open("updateXml.xml");
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(is);
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        if (document == null)
        {
            return null;
        }

        UpdateDbXml xml = new UpdateDbXml(document);

        return xml;
    }
    /**
     * 获取APK版本号
     *
     * @param context 上下文
     * @return 版本号
     * @throws throws [违例类型] [违例说明]
     * @see
     */
    public String getVersionName(Context context) {
        String versionName = null;
        try
        {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;

        } catch (PackageManager.NameNotFoundException e)
        {
        }

        return versionName;
    }

    /**
     * 保存下载APK版本信息
     *
     * @param context
     * @return 保存成功返回true，否则返回false
     * @throws throws [违例类型] [违例说明]
     * @see
     */
    public  boolean saveVersionInfo(Context context,String newVersion) {
        boolean ret = false;

        FileWriter writer = null;
        try
        {
            writer = new FileWriter(new File(parentFile,"update.txt"), false);
            writer.write("V003"+ INFO_FILE_DIV +"V002");
            writer.flush();
            ret = true;
        } catch (IOException e)
        {
        } finally
        {
            if (writer != null)
            {
                try
                {
                    writer.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    /**
     * 获取本地版本相关信息
     *
     * @return 获取数据成功返回true，否则返回false
     * @throws throws [违例类型] [违例说明]
     * @see
     */
    private String existVersion;
    private String lastBackupVersion;
    private boolean getLocalVersionInfo() {
        boolean ret = false;

        File file = new File(parentFile,"update.txt");

        if (file.exists())
        {
            int byteread = 0;
            byte[] tempbytes = new byte[100];
            StringBuilder stringBuilder = new StringBuilder();
            InputStream in = null;
            try
            {
                in = new FileInputStream(file);
                while ((byteread = in.read(tempbytes)) != -1)
                {
                    stringBuilder.append(new String(tempbytes, 0, byteread));
                }
                String[] infos = stringBuilder.toString().split(INFO_FILE_DIV);
                if (infos.length == 2)
                {
                    existVersion = infos[0];
                    lastBackupVersion = infos[1];
                    ret = true;
                }
            } catch (Exception e)
            {

            } finally
            {
                if (null != in)
                {
                    try
                    {
                        in.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    in = null;
                }
            }
        }

        return ret;
    }
}
