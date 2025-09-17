package kr.spot.common;

import kr.spot.domain.Study;
import kr.spot.domain.vo.Fee;

public class StudyFixture {
    public static Long ID = 1L;
    public static String NAME = "Study Name";
    public static String DESCRIPTION = "Study Description";
    public static String IMAGE_URL = "http://example.com/image.png";
    public static int MAX_MEMBERS = 10;
    public static boolean HAS_FEE = true;
    public static int FEE_AMOUNT = 1_000;

    public static Study study() {
        return Study.of(ID, NAME, MAX_MEMBERS, IMAGE_URL, Fee.of(HAS_FEE, FEE_AMOUNT), DESCRIPTION);
    }

}
