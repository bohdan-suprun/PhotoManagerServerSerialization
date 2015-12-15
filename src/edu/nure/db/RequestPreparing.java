package edu.nure.db;

import edu.nure.db.primarykey.PrimaryKey;

public class RequestPreparing {

    public static final String DB_NAME = "pmanager";

    public static String update(String table, String[] colNames, Object[] values, String whereCond) {
        StringBuilder req = new StringBuilder("UPDATE `" + DB_NAME + "`.`" + table.toUpperCase() + "` SET ");
        for (int i = 0; i < colNames.length; i++) {
            if (values[i] != null) {
                if (values[i] instanceof String) {
                    req.append("`").append(colNames[i]).append("`= '").append(values[i].toString().replace("\"", "\\\"").trim()).append("', ");
                } else {
                    req.append("`").append(colNames[i]).append("`= ").append(values[i].toString().trim()).append(", ");
                }
            } else {
                req.append("`").append(colNames[i]).append("`= NULL, ");
            }
        }
        req = new StringBuilder(req.substring(0, req.toString().lastIndexOf(',')));
        if (whereCond != null) {
            req.append(" WHERE ").append(whereCond);
        }
        return req + ";";
    }

    public static String insert(String table, String[] colNames, Object[] values) {
        StringBuilder req = new StringBuilder("INSERT INTO `" + DB_NAME + "`.`" + table.toUpperCase() + "`(");
        for (String v : colNames)
            req.append("`").append(v).append("`, ");
        req = new StringBuilder(req.substring(0, req.length() - 2)).append(") Values(");
        for (int i = 0; i < colNames.length; i++) {
            if (values[i] != null) {
                if (values[i] instanceof String) {
                    req.append("'").append(values[i].toString().replace("\"", "\\\"").trim()).append("', ");
                } else {
                    req.append(values[i].toString().trim()).append(", ");
                }
            } else {
                req.append("NULL, ");
            }
        }
        return req.substring(0, req.toString().lastIndexOf(',')) + ");";
    }

    public static String select(String table, String[] colNames, String add) {
        String req = "SELECT ";
        if (colNames.length == 1 && colNames[0].equals("*")) {
            req += "*";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String v : colNames) {
                sb.append("`").append(v).append("`, ");
            }
            req += sb.substring(0, sb.length() - 2);
        }
        req += " FROM `" + DB_NAME + "`." + table.toUpperCase();
        if (add != null) {
            req += " " + add;
        }
        return req + ";";
    }

    public static String select(String table, String[] colNames, PrimaryKey pk) {
        String req = "SELECT ";
        if (colNames.length == 1 && colNames[0].equals("*")) {
            req += "*";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String v : colNames) {
                sb.append("`").append(v).append("`, ");
            }
            req += sb.substring(0, sb.length() - 2);
        }
        req += " FROM `" + DB_NAME + "`." + table.toUpperCase();
        if (pk != null) {
            req += " WHERE `" + pk.getName() + "` = ";
            if (pk.getValue() instanceof String) {
                req += "'" + pk.getValue() + "'";
            } else {
                req += pk.getValue();
            }
        }
        return req + ";";
    }

    public static String join(String table, String type, String onCond) {
        return type + " JOIN `" + DB_NAME + "`." + table.toUpperCase() + " ON " + onCond + " ";
    }
}
