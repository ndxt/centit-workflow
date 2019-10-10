package com.centit.workflow.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName RoleFormula/ 权限表达式的 快捷键； 这样在 流程中可以 灵活的使用权限表达式
 * @Date 2019/7/22 15:32
 * @Version 1.0
 */
@Data
@Entity
@Table(name = "WF_ROLE_FORMULA")
public class RoleFormula implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "FORMULA_CODE")
    @ValueGenerator(strategy = GeneratorType.UUID)
    private String formulaCode;

    @Column(name = "FORMULA_NAME")
    private String formulaName;

    /**
     * 权限表达式
     */
    @Column(name = "ROLE_FORMULA")
    private String roleFormula;

    @Column(name = "ROLE_LEVEL")
    private int roleLevel;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_TIME")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, value = "today()")
    private Date createTime;

}
