package model.enums;

public enum Role {
    QUAN_TRI("Quản trị"),
    NHAN_VIEN("Nhân viên");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString(){
        return role;
    }
}
