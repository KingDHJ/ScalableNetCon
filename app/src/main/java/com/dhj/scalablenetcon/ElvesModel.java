package com.dhj.scalablenetcon;

import java.util.List;

/**
 * Created by duanhuangjun on 17/2/27.
 */

public class ElvesModel {
        private int code;
        private List<Modle> modleList;
        private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Modle> getModleList() {
        return modleList;
    }

    public void setModleList(List<Modle> modleList) {
        this.modleList = modleList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class Modle{
            private String title;
            private String kinds;
            private String ids;
            private String summary;
            private String advertPic;
            private String advertId;
            private String advertClass;
            private String time;
            private String url;
            private String state;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getKinds() {
                return kinds;
            }

            public void setKinds(String kinds) {
                this.kinds = kinds;
            }

            public String getIds() {
                return ids;
            }

            public void setIds(String ids) {
                this.ids = ids;
            }

            public String getSummary() {
                return summary;
            }

            public void setSummary(String summary) {
                this.summary = summary;
            }

            public String getAdvertPic() {
                return advertPic;
            }

            public void setAdvertPic(String advertPic) {
                this.advertPic = advertPic;
            }

            public String getAdvertId() {
                return advertId;
            }

            public void setAdvertId(String advertId) {
                this.advertId = advertId;
            }

            public String getAdvertClass() {
                return advertClass;
            }

            public void setAdvertClass(String advertClass) {
                this.advertClass = advertClass;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
    }
}
