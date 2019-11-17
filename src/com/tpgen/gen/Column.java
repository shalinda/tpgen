package com.tpgen.gen;

public class Column {

    private int index;

    private String colName;

	private Integer colType;

	private String varName;

	private String methodName;

    private String title;

    private int length;

    private String javaType;

    private String mongoType;

    private boolean fk;

    private String fkVarName;

    private String lowerName;

    private String fkMethodName;

    private String fkKey;

    private String fkName;

    private String sample;

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String getLowerName() {
        return lowerName;
    }

    public void setLowerName(String lowerName) {
        this.lowerName = lowerName;
    }

    public String getFkVarName() {
        return fkVarName;
    }

    public void setFkVarName(String fkVarName) {
        this.fkVarName = fkVarName;
    }

    public String getFkMethodName() {
        return fkMethodName;
    }

    public void setFkMethodName(String fkMethodName) {
        this.fkMethodName = fkMethodName;
    }

    public String getFkKey() {
        return fkKey;
    }

    public void setFkKey(String fkKey) {
        this.fkKey = fkKey;
    }

    public String getFkName() {
        return fkName;
    }

    public void setFkName(String fkName) {
        this.fkName = fkName;
    }

    public boolean isFk() {
        return fk;
    }

    public void setFk(boolean fk) {
        this.fk = fk;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
	 * @return Returns the colName.
	 */
	public String getColName() {
		return colName;
	}

	/**
	 * @param colName The colName to set.
	 */
	public void setColName(String colName) {
		this.colName = colName;
	}

	/**
	 * @return Returns the colType.
	 */
	public Integer getColType() {
		return colType;
	}

	/**
	 * @param colType The colType to set.
	 */
	public void setColType(Integer colType) {
		this.colType = colType;
	}

	/**
	 * @return Returns the methodName.
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName The methodName to set.
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * @return Returns the varName.
	 */
	public String getVarName() {
		return varName;
	}

	/**
	 * @param varName The varName to set.
	 */
	public void setVarName(String varName) {
		this.varName = varName;
	}

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setMongoType(String mongoType) {
        this.mongoType = mongoType;
    }

    public String getMongoType() {
        return mongoType;
    }    

    public Boolean getIsDate() {
        return "DateTime".equals(javaType);
    }
}
