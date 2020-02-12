package lv.igors.lottery.statusResponse;

public enum Responses {
    FAIL("Fail"),
    OK("OK"),
    UNKNOWN_ERROR("Unexpected error"),
    LOTTERY_REGISTER_INACTIVE("Registration is inactive"),
    LOTTERY_REGISTER_ACTIVE("Registration is active"),
    LOTTERY_EXCESS_PARTICIPANTS("Too many participants"),
    LOTTERY_FINISHED("Lottery is finished"),
    LOTTERY_STATUS_PENDING("PENDING"),
    LOTTERY_NON_EXIST("Lottery does not exist"),
    LOTTERY_NO_PARTICIPANTS("No participants in lottery"),
    CODE_EXIST("Code already exists"),
    CODE_FOREIGN_CODE("The code is not yours"),
    CODE_WIN("WIN"),
    CODE_LOSE("LOSE"),
    CODE_NON_EXIST("Code doesnt exist"),
    CODE_INVALID("Invalid code");

    private String title;

    Responses(String title) {
        this.title = title;
    }

    public String getResponse() {
        return title;
    }

    @Override
    public String toString() {
        return "Responses{" +
                "title='" + title + '\'' +
                '}';
    }

}
