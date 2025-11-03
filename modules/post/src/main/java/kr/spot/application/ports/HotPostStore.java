package kr.spot.application.ports;

import java.util.List;

public interface HotPostStore {
    void replaceTop3(List<Long> postIds);
    
    List<Long> getTop3();
}
