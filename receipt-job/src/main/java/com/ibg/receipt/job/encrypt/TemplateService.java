package com.ibg.receipt.job.encrypt;

import com.ibg.receipt.util.EncryptUtil;
import com.ibg.receipt.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TemplateService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<com.ibg.receipt.job.encrypt.EncryptQueryResult> query(String tableName, Long lastId, Integer pageSize, List<String> fieldName) {
        String sql = getQuerySql(tableName);
        try {
            List list = jdbcTemplate.query(sql, new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement) throws SQLException {
                    preparedStatement.setLong(1, lastId);
                    preparedStatement.setLong(2, lastId + pageSize);
                }
            }, new ResultSetExtractor<List>() {
                @Override
                public List extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                    List list1 = new ArrayList();
                    while (resultSet.next()){
                        com.ibg.receipt.job.encrypt.EncryptQueryResult result = new com.ibg.receipt.job.encrypt.EncryptQueryResult();
                        result.setId(resultSet.getLong("id"));
                        EncryptValueData data = new EncryptValueData();
                        List<EncryptValueData> valueList = new ArrayList<>();
                        for(String name : fieldName){
                            if(StringUtils.isNotBlank(resultSet.getString(name)) && !EncryptUtil.isEncode(resultSet.getString(name))){
                                data = new EncryptValueData();
                                data.setValueFiledName(name);
                                data.setValue(EncryptUtil.encode(resultSet.getString(name)));
                                valueList.add(data);
                            }
                        }
                        result.setValue(valueList);
                        list1.add(result);
                    }
                    return list1;
                }
            });
            return list;
        } catch (Exception e) {
            return null;
        }
    }


    private String getUpdateSql(String tableName, List<EncryptValueData> list, Long id){
        StringBuilder sb = new StringBuilder();
        StringBuilder updateSb = new StringBuilder();
        for(EncryptValueData encryptValueData:list){
            if(StringUtils.isNotBlank(encryptValueData.getValue())){
                updateSb.append(encryptValueData.getValueFiledName()).append(" = '").append(encryptValueData.getValue()).append("',");
            }
        }
        //所有字段均已加密，则不进行更新
        if(updateSb.length() == 0){
            return null;
        }
        updateSb.deleteCharAt(updateSb.length()-1);
        return  sb.append(" update ").append(tableName).append(" set ").append(updateSb.toString())
            .append(" where id = ").append(id).toString();

    }
    private String getQuerySql(String tableName){
        StringBuilder sb = new StringBuilder();
        return  sb.append(" select * from ").append(tableName).append(" where id between ? and ? order by id asc" ).toString();

    }


    public int updateBatch(List<com.ibg.receipt.job.encrypt.EncryptQueryResult> list, String tableName ) {
        List<String> sqls = new ArrayList<>();

        for(com.ibg.receipt.job.encrypt.EncryptQueryResult result:list){
            String sql = getUpdateSql(tableName, result.getValue(),result.getId());
            if(sql != null){
                sqls.add(sql);
            }
        }
        if(sqls.size() > 0){
            int[] res = jdbcTemplate.batchUpdate(sqls.toArray(new String[sqls.size()]));
        }
        return 0;
    }

    public Integer count(String tableName) {
        String sql = getCountSql(tableName);
        return jdbcTemplate.queryForObject(sql,java.lang.Integer.class);
    }

    private String getCountSql(String tableName) {
        StringBuilder sb = new StringBuilder();
        return  sb.append(" select count(1) from ").append(tableName).toString();
    }
}
