public class Result {
    private boolean isRightType;
    private boolean isRightBounds;
    private boolean isOrphan;

    public Result(boolean isRightType, boolean isRightBounds, boolean isOrphan) {
        this.isRightType = isRightType;
        this.isRightBounds = isRightBounds;
        this.isOrphan = isOrphan;
    }

    public int determineResult() {
        if (isRightType && isRightBounds) {
            return 1;
        }
        else if (isOrphan) {
           return 2;
        }
        else if (isRightType && !isRightBounds) {
            return 3;
        }
        else if (!isRightType && isRightBounds) {
            return 4;
        }
        else if (!isRightType && !isRightBounds) {
            return 5;
        }
        return 0;

    }

    public boolean isRightType() {
        return isRightType;
    }

    public void setRightType(boolean rightType) {
        isRightType = rightType;
    }

    public boolean isRightBounds() {
        return isRightBounds;
    }

    public void setRightBounds(boolean rightBounds) {
        isRightBounds = rightBounds;
    }

    public boolean isOrphan() {
        return isOrphan;
    }

    public void setOrphan(boolean orphan) {
        isOrphan = orphan;
    }
}

