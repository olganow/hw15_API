package models;

import lombok.Data;

@Data
public class UserSingleResponseModel {
    private DataModel data;
    private SupportModel support;

    @Data
    public static class DataModel {
        private int id;
        private String email;
        private String first_name;
        private String last_name;
        private String avatar;
    }

    @Data
    public static class SupportModel {
        private String url;
        private String text;
    }
}
