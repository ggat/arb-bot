package ge.shitbot.core.datatypes;

import ge.shitbot.core.datatypes.exceptions.CoreException;

/**
 * Created by giga on 11/24/17.
 */
public enum OddType {
    _1,
    _X,
    _2,
    _1X,
    _12,
    _X2,
    _UNDER_25,
    _OVER_25,
    _YES,
    _NO,
    __UNKNOWN;

    private static OddType[][] contraryList = {
            {_1, _X2},
            {_X, _12},
            {_2, _1X},
            {_UNDER_25, _OVER_25},
            {_YES, _NO},
    };

    public OddType contrary() {

        OddType resultContrary = OddType.__UNKNOWN;

        for (OddType[] contrary : contraryList) {
            if(contrary.length != 2){
                //throw new CoreException();
            }

            resultContrary = contrary[0] == this ? contrary[1] : contrary[0];
        }

        return resultContrary;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
