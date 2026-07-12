package com.professional.b07legendaryproject2026.data;

import java.util.ArrayList;
import java.util.List;

public class ArtifactRepository {

    public List<Artifact> getArtifacts(int count) {
        List<Artifact> artifacts = new ArrayList<>();
        for (int i = 0; i <= count; i++) {
            Artifact a = new Artifact();
            if(i == 0) {
                a.setLotNumber("rawad-001");
                a.setName("rawad");
            } else {
                a.setLotNumber("LOT-"+String.format("%03d", i));
                a.setName("Item #"+i);
                a.setImage("https://picsum.photos/400?frick_cache="+System.nanoTime());
            }
            a.setPeriodNum(Artifact.PeriodNum.values()[i % 19]);
            artifacts.add(a);
        }
        return artifacts;
    }
}
