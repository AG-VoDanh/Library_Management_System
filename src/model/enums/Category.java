package model.enums;

public enum Category {
    Cong_Nghe_Thong_Tin("Công nghệ thông tin"),
    Van_Hoc("Văn học"),
    Ky_Nang_Song("Kỹ năng sống"),
    Khoa_Hoc("Khoa học"),
    Kinh_Te("Kinh tế");

    private final String category;

    Category(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString(){
        return category;
    }
}
