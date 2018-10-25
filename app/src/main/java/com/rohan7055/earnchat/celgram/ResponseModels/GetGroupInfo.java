package com.rohan7055.earnchat.celgram.ResponseModels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ron on 4/8/2017.
 */

public class GetGroupInfo
{
    @SerializedName("status")
    String status;

    @SerializedName("message")
    String message;

    @SerializedName("data")
    Data data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    class Data
    {
       @SerializedName("group_id")
        String group_id;

        @SerializedName("group_name")
        String group_name;

        @SerializedName("gr_dp_url")
        String gr_dp_url;

        @SerializedName("groups_members")
        String groups_members;

        @SerializedName("admin")
        String admin;

        @SerializedName("created_at")
        String created_at;

        @SerializedName("deleted")
        String deleted;

        public String getGroup_name() {
            return group_name;
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getGr_dp_url() {
            return gr_dp_url;
        }

        public void setGr_dp_url(String gr_dp_url) {
            this.gr_dp_url = gr_dp_url;
        }

        public String getGroups_members() {
            return groups_members;
        }

        public void setGroups_members(String groups_members) {
            this.groups_members = groups_members;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getAdmin() {
            return admin;
        }

        public void setAdmin(String admin) {
            this.admin = admin;
        }

        public String getDeleted() {
            return deleted;
        }

        public void setDeleted(String deleted) {
            this.deleted = deleted;
        }
    }
}
