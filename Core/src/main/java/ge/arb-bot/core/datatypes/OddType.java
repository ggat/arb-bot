package ge.arb-bot.core.datatypes;

import ge.arb-bot.core.datatypes.exceptions.CoreException;

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

            if(contrary[0] == this || contrary[1] == this) {
                resultContrary = contrary[0] == this ? contrary[1] : contrary[0];
                break;
            }
        }

        return resultContrary;
    }

    public String stringValue() {

        String result = null;

        switch (this) {
            case _1:
                result = "1";
                break;
            case _X:
                result = "X";
                break;
            case _2:
                result = "2";
                break;
            case _1X:
                result = "1X";
                break;
            case _12:
                result = "12";
                break;
            case _X2:
                result = "X2";
                break;
            case _UNDER_25:
                result = "UNDER";
                break;
            case _OVER_25:
                result = "OVER";
                break;
            case _YES:
                result = "YES";
                break;
            case _NO:
                result = "NO";
                break;
            case __UNKNOWN:
                result = "UNKNOWN";
                break;
        }

        return result;
    }

    public static void main(String[] args) {
        OddType oddType = OddType._X;

        oddType.contrary();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
