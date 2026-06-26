package vn.edu.fpt.validation;

public class UrlValition {
    public static Integer getIntegerId(String id) {
        Integer idInt = null;
        try {
            idInt = Integer.parseInt(id);
        } catch (Exception e) {
            System.out.println("Invalid id!");
            return null;
        }
        return idInt;
    }
}
