package com.example.d2d;

public class LoginResponse {
    private String status;
    private String user_role;
    private int user_id;

    public LoginResponse(String status,String user_role,int user_id){
        this.status=status;
        this.user_role=user_role;
        this.user_id=user_id;
    }

    public int getUser_id() {
        return user_id;
    }
    public String getStatus(){
        return status;
    }

    public String getRole(){
        return user_role;
    }
}
