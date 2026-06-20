package com.nhom1;public class Auth {
    public static String user = null;
    public static String role = null;
    public static String maNV = null;

    public static boolean isAdmin() {
        return role != null && role.equalsIgnoreCase("Admin");
    }

    public static boolean isGiangVien() {
        return role != null && role.equalsIgnoreCase("GiangVien");
    }
    
    public static void clear() {
        user = null;
        role = null;
        maNV = null;
    }
}