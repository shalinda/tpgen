/**
 *
 */
package com.tpgen.gen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.tpgen.common.Global;
import com.tpgen.common.FrameworkException;

/**
 * @author Shalinda Ranasinghe <shalinda@tpgen.com>
 */

public class UI7 {

    /** Velocity context which exposes our objects in the templates. */
    protected Context context;
    protected String module;
    private String[] prefixJava;
    private String prefixResource = "resources/";
    private String prefixWeb = "web/";
    private String prefixAux = "aux/";
    private String table;
    private VelocityEngine ve;
    private String[] pkg;
    private String className;
    private String templates;
    private String db;

    private final static Map<String, String[]> escapes = new HashMap<String, String[]>();

    private static Logger log = Logger.getLogger(UI7.class);
    private List<Column> cols;
    private boolean isJavaTransform;

    private static final String TEMPLATE_DIR = "templates/ui7";
    private String dir1;
    private String objectName;
    private String app;
    private boolean isJs;
    private String tableName;

    {
        escapes.put("MYSQL", "`:`".split(":"));
        escapes.put("SQLSVR", "[:]".split(":"));
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public UI7(String app, String table, String db, String templates, String pkg, String dir1, String isJs)
            throws Exception {
        String[] tables1 = table.split("->"); /// fk
        String[] tempTable = tables1[0].split("_");
        if (tempTable[0].length() == 1)
            this.tableName = tables1[0].substring(2);
        else
            this.tableName = tables1[0];

        this.table = tables1[0];

        this.isJs = isJs.equals("true");
        this.app = app;
        this.pkg = pkg.split(":");
        prefixJava = new String[this.pkg.length];
        int i = 0;
        for (String pkg1 : this.pkg) {
            prefixJava[i++] = pkg1.replaceAll("[.]", "/") + "/";
        }

        this.dir1 = dir1;
        // this.module = module;
        this.templates = templates;
        this.db = db;

        cols = getColumnDetails();
        ArrayList<Column> fkcols = new ArrayList<Column>();

        for (i = 0; i < tables1.length; i++) {
            if (i > 0) {
                Column col;
                String[] tbcols = tables1[i].split(",");
                if (tbcols.length != 4)
                    throw new RuntimeException("table spec format error in fk [4 , seperated cols needed] " + tables1[0]
                            + " cols " + tables1[i]);
                col = findFK(tbcols[0], tbcols[1], tbcols[2], tbcols[3]);
                fkcols.add(col);
            }

        }

        this.objectName = StringUtils.uncapitalize(className);
        // pkg = toPackageName(module, " ");
        context = new VelocityContext();
        context.put("app", app);
        context.put("module", module);
        context.put("db", db);
        context.put("table", tableName);
        context.put("package", pkg);
        // context.put("constant", toConstantName(module, " "));
        context.put("className", className);
        context.put("objectName", objectName);
        context.put("cols", cols);
        context.put("pk", cols.get(0));
        context.put("fkcols", fkcols);

        ArrayList<Column> datecols = new ArrayList<Column>();
        for (Column col : cols) {
            if (col.getColType() == 93)
                datecols.add(col);
        }
        context.put("dates", datecols);

        ve = new VelocityEngine();
        ve.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, TEMPLATE_DIR);
        ve.init();
    }

    private Column findFK(String sourceCol, String destTable, String destKey, String destName) {
        for (Column col : cols) {
            if (sourceCol.equals(col.getColName())) {
                col.setFk(true);
                col.setFkMethodName(toJavaName(destTable, "_", false));
                col.setFkVarName(toJavaName(destTable, "_", true));
                col.setFkKey(toJavaName(destKey, "_", true));
                col.setFkName(toJavaName(destName, "_", true));
                return col;
            }
        }
        throw new RuntimeException("fk field not found " + sourceCol);
    }

    /**
     * Method should be implemented differently for different frameworks/ or a
     * collaboration of classes who achieves a collective task
     *
     */
    public void execute() {
        FileWriter writer = null;
        String app = this.app.replace(".", "/");
        try {
            String[] templates1 = templates.split(":");
            int i = 0;
            for (String template : templates1) {
                String[] template1 = template.split(",");
                StringBuffer path = new StringBuffer();
                if (template1.length > 2) {
                    path.append(template1[2].replace(".", "/"));
                } else {
                    path.append(prefixJava[prefixJava.length == 1 ? 0 : i++]);
                }
                if (template1[0].indexOf("pkg") > -1) {
                    path.insert(0, "src/com/" + app + "/").append("/" + objectName);
                } else if (template1[1].indexOf("java") > -1 || template1[1].indexOf("hbm.xml") > -1) {
                    path.insert(0, "src/com/" + app + "/");
                } else if (template1[0].indexOf("web") > -1) {
                    path.append("/").append(objectName);
                }

                String fileName;
                if (template1[1].indexOf("struts") > -1) {
                    fileName = className.toLowerCase();
                } else if (template1[0].indexOf("pkg") > -1) {
                    fileName = "package";
                } else {
                    fileName = isJs ? className.toLowerCase() : className;
                }
                generateObject(ve, path.toString(), template1[0] + ".vm", fileName + template1[1]);
            }
        } catch (Exception e) {
            log.error("Error on executing UI7", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void generateObject(VelocityEngine ve, String directory, String template, String file) {
        FileWriter writer = null;
        try {
            File dir = new File(dir1 + directory);
            if (!dir.exists()) {
                // @todo check if actually created
                log.debug(dir.getAbsolutePath());
                log.debug("mkdir : " + dir.mkdirs());
            }
            File child = new File(dir, file);
            if (!child.exists()) {
                child.delete();
            }
            child.createNewFile();

            Template t = ve.getTemplate(template);
            writer = new FileWriter(child);
            t.merge(context, writer);
            writer.flush();
            log.info("merging template:" + template + " > " + child.getAbsolutePath());
        } catch (Exception e) {
            log.error("Error on executing UI7", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }

    }

    public static void main(String[] args) throws Exception {
        Class.forName("com.tpgen.common.Global").newInstance();
        Properties config = Global.getConfig();
        String tables[] = config.getProperty("table").split(":");

        for (String table : tables) {
            UI7 gen = new UI7(config.getProperty("app"), table, config.getProperty("db"),
                    config.getProperty("templates"), config.getProperty("pkg"), config.getProperty("dir"),
                    config.getProperty("isJs"));
            gen.execute();
        }
    }

    private List<Column> getColumnDetails() throws FrameworkException {
        List<Column> cols = new ArrayList<Column>();
        Column col = null;
        Connection conn = null;
        String colName = null;
        Integer colType = null;
        String varName = null;
        String methodName = null;

        try {
            conn = Global.connect();
            Statement stmt = conn.createStatement();

            ResultSet rs;

            if ("java".equals(table)) {
                rs = stmt.executeQuery("SELECT * FROM " + dbEscape(table) + " where active=1 order by field_order");
                log.debug("transform java table");
                cols = doJavaTransform(rs);
            } else {
                rs = stmt.executeQuery("SELECT * FROM " + dbEscape(table));
                className = toJavaName(tableName, "_", false);
                ResultSetMetaData meta = rs.getMetaData();
                log.debug("table : " + table);

                for (int i = 1; i < meta.getColumnCount() + 1; i++) {
                    col = new Column();
                    colName = meta.getColumnName(i);
                    colType = new Integer(meta.getColumnType(i));
                    col.setIndex(i - 1);
                    col.setColName(colName);
                    col.setColType(colType);
                    col.setJavaType(toJavaType(colType));
                    col.setMongoType(toMongoType(colType));
                    col.setSample("Integer".equals(col.getJavaType()) ? "11111111111" : "abc");
                    col.setTitle(toModuleTitle(colName, "_"));
                    // col.setLength(meta.getPrecision(i));
                    varName = toJavaName(colName, "_", true);
                    methodName = toJavaName(colName, "_", false);
                    col.setVarName(varName);
                    col.setMethodName(methodName);
                    cols.add(col);
                }
            }
        } catch (SQLException e) {
            log.error(e);
            throw new FrameworkException("Error getting database info", e);
        } finally {
            Global.close();
        }

        return cols;
    }

    private List doJavaTransform(ResultSet rs) {
        log.debug("table : " + table);
        isJavaTransform = true;
        List<Column> cols = new ArrayList();
        Column col;
        String colName;
        int colType = 0;
        int i = 1;
        String varName;
        String methodName;
        try {
            while (rs.next()) {
                className = rs.getString("class"); // inefficient
                col = new Column();
                colName = rs.getString("field");
                col.setIndex(i - 1);
                col.setColName(colName);
                col.setColType(colType);
                col.setJavaType(rs.getString("type"));
                col.setLength(1);
                varName = colName;
                methodName = StringUtils.capitalize(colName);
                col.setVarName(varName);
                col.setMethodName(methodName);
                cols.add(col);
            }
        } catch (SQLException e) {
            log.error(e, e);
        }
        return cols;
    }

    private String dbEscape(String table) {
        String[] escapeArray = escapes.get(db);

        return escapeArray == null ? table : escapeArray[0] + table + escapeArray[1];
    }

    private static String toJavaName(String colName, String separator, boolean lowerFirst) {
        String javaName = "";
        StringTokenizer st = new StringTokenizer(colName, separator, false);
        while (st.hasMoreElements()) {
            String element = (String) st.nextElement();
            if (lowerFirst) {
                javaName += StringUtils.lowerCase(element);
                lowerFirst = false;
            } else {
                element = StringUtils.lowerCase(element);
                javaName += StringUtils.capitalize(element);
            }
        }
        return javaName;
    }

    private static String toPackageName(String colName, String separator) {
        String newName = "";
        StringTokenizer st = new StringTokenizer(colName, separator, false);
        while (st.hasMoreElements()) {
            String element = (String) st.nextElement();
            newName += StringUtils.lowerCase(element);
        }
        return newName;
    }

    private static String toConstantName(String colName, String separator) {
        String newName = "";
        StringTokenizer st = new StringTokenizer(colName, separator, false);
        while (st.hasMoreElements()) {
            String element = (String) st.nextElement();
            newName += StringUtils.upperCase(element);
            if (st.hasMoreElements()) {
                newName += "_";
            }
        }
        return newName;
    }

    private static String toModuleTitle(String colName, String separator) {
        String newName = "";
        StringTokenizer st = new StringTokenizer(colName, separator, false);
        while (st.hasMoreElements()) {
            String element = (String) st.nextElement();
            // element += StringUtils.lowerCase(element);
            newName += StringUtils.capitalize(element);
            if (st.hasMoreElements()) {
                newName += " ";
            }
        }
        return newName;
    }

    private String toJavaType(int sqlType) {
        String type = "";

        // log.debug("sqlType :" + sqlType);
        switch (sqlType) {
        case Types.CHAR:
        case Types.VARCHAR:
            type = "String";
            break;
        case Types.BIGINT:
        case Types.INTEGER:
            type = "Integer";
            break;
        case Types.DECIMAL:
        case Types.NUMERIC:
            type = "Integer"; // Double
            break;
        case Types.DOUBLE:
            type = "Double"; // Double
            break;
        case Types.DATE:
        case Types.TIMESTAMP:
            type = "Date";
            break;
        case Types.BIT:
            type = "Boolean";
            break;
        case Types.LONGVARBINARY:
            type = "Byte[]";
            break;
        default:
        }
        return type;
    }

    private String toMongoType(int sqlType) {
        String type = "";

        // log.debug("sqlType :" + sqlType);
        switch (sqlType) {
        case Types.CHAR:
        case Types.LONGVARCHAR:
        case Types.VARCHAR:
            type = "String";
            break;
        case Types.BIGINT:
        case Types.SMALLINT:
        case Types.INTEGER:
        case Types.DECIMAL:
        case Types.NUMERIC:
        case Types.DOUBLE:
            type = "Number"; // Double
            break;
        case Types.DATE:
        case Types.TIMESTAMP:
            type = "Date";
            break;
        case Types.BIT:
            type = "Boolean";
            break;
        case Types.LONGVARBINARY:
            type = "Byte[]";
            break;
        default:
        }
        return type;
    }

}