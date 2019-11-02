public class Result {
    private String result;
    private boolean isRightType;
    private boolean isRightBounds;
    private boolean isOrphan;

    public Result(boolean isRightType, boolean isRightBounds, boolean isOrphan) {
        this.isRightType = isRightType;
        this.isRightBounds = isRightBounds;
        this.isOrphan = isOrphan;
        determineResult();
    }

    private void determineResult() {
        if (isRightType && isRightBounds) {
            this.result = "Type & Bounds";
        }
        else if (isOrphan) {
           this.result = "Missed";
        }
        else if (isRightType && !isRightBounds) {
            this.result = "Type & ~Bounds";
        }
        else if (!isRightType && isRightBounds) {
            this.result = "~Type & Bounds";
        }
        else if (!isRightType && !isRightBounds) {
            this.result = "~Type & ~Bounds";
        }

    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

