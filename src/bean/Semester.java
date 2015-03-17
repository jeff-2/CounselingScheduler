package bean;

public class Semester {
	public static final int SPRING = 0;
	public static final int SUMMER = 1;
	public static final int FALL = 2;
	public static final int WINTER = 3;
	
	public static String asString(int semester) {
		switch(semester) {
		case 0:
			return "Spring";
		case 1:
			return "Summer";
		case 2:
			return "Fall";
		case 3:
			return "Winter";
		default:
			throw new IllegalArgumentException("Invalid semester");
		}
		
	}
	
	public static int[] getValues() {
		return new int[] {0, 1, 2, 3};
	}
}
