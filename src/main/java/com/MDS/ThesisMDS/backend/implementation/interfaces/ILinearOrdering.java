package com.MDS.ThesisMDS.backend.implementation.interfaces;


import com.MDS.ThesisMDS.backend.implementation.objects.LinearPoint;

import java.util.ArrayList;


public interface ILinearOrdering {
    ArrayList<LinearPoint> calculatePoints(Integer width, Integer height, Integer bits, Integer margin);
}
