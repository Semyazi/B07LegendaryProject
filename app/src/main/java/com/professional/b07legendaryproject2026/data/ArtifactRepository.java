package com.professional.b07legendaryproject2026.data;

import java.util.ArrayList;
import java.util.List;

public class ArtifactRepository {
    
    public List<Artifact> getArtifacts(int count) {
        List<Artifact> artifacts = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Artifact a = new Artifact();
            a.setLotNumber("rawad-" + String.format("%03d", i));
            a.setName("rawad " + i);
            a.setPeriodNum(Artifact.PeriodNum.FIVE_DYNASTIES_AND_TEN_KINGDOMS);
            if (i == 1) {
                a.setImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQNo24-TgibHQu-TJ4mons88cFjtyJ1kea_dvoBKt_bEPBwe8Zv8i10l_8&s=10");
            }
            artifacts.add(a);
        }
        return artifacts;
    }
}
