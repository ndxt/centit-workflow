/*待办和已办任务视图的修改添加*/
/**待办**/
create or replace view V_INNER_USER_TASK_LIST as
SELECT
	`a`.`FLOW_INST_ID` AS `FLOW_INST_ID`,
	`w`.`FLOW_CODE` AS `FLOW_CODE`,
	`w`.`VERSION` AS `version`,
	`w`.`FLOW_Opt_Name` AS `FLOW_OPT_NAME`,
	`w`.`FLOW_Opt_Tag` AS `FLOW_OPT_TAG`,
	`a`.`NODE_INST_ID` AS `NODE_INST_ID`,
	ifnull(
		`a`.`UNIT_CODE`,
		ifnull(`w`.`UNIT_CODE`, '0000000')
	) AS `UnitCode`,
	`a`.`USER_CODE` AS `user_code`,
	`c`.`ROLE_TYPE` AS `ROLE_TYPE`,
	`c`.`ROLE_CODE` AS `ROLE_CODE`,
	'一般任务' AS `AUTHDESC`,
	`c`.`NODE_CODE` AS `node_code`,
	`c`.`NODE_NAME` AS `Node_Name`,
	`c`.`NODE_TYPE` AS `Node_Type`,
	`c`.`OPT_TYPE` AS `NODEOPTTYPE`,
	`c`.`OPT_PARAM` AS `Opt_Param`,
	`a`.`CREATE_TIME` AS `CREATE_TIME`,
	`a`.`promise_Time` AS `Promise_Time`,
	`a`.`time_Limit` AS `TIME_LIMIT`,
	`c`.`OPT_CODE` AS `OPT_CODE`,
	`c`.`Expire_Opt` AS `Expire_Opt`,
	`c`.`STAGE_CODE` AS `STAGE_CODE`,
	`a`.`last_update_user` AS `last_update_user`,
	`a`.`last_update_time` AS `LAST_UPDATE_TIME`,
	`w`.`INST_STATE` AS `inst_state`
FROM
	(
		(
			`wf_node_instance` `a`
			JOIN `wf_flow_instance` `w` ON (
				(
					`a`.`FLOW_INST_ID` = `w`.`FLOW_INST_ID`
				)
			)
		)
		JOIN `wf_node` `c` ON (
			(
				`a`.`NODE_ID` = `c`.`NODE_ID`
			)
		)
	)
WHERE
	(
		(`a`.`NODE_STATE` = 'N')
		AND (`w`.`INST_STATE` = 'N')
		AND (`a`.`TASK_ASSIGNED` = 'S')
	)
UNION ALL
	SELECT
		`a`.`FLOW_INST_ID` AS `FLOW_INST_ID`,
		`w`.`FLOW_CODE` AS `FLOW_CODE`,
		`w`.`VERSION` AS `version`,
		`w`.`FLOW_Opt_Name` AS `FLOW_OPT_NAME`,
		`w`.`FLOW_Opt_Tag` AS `FLOW_OPT_TAG`,
		`a`.`NODE_INST_ID` AS `NODE_INST_ID`,
		ifnull(
			`a`.`UNIT_CODE`,
			ifnull(`w`.`UNIT_CODE`, '0000000')
		) AS `UnitCode`,
		`b`.`USER_CODE` AS `user_code`,
		`b`.`ROLE_TYPE` AS `ROLE_TYPE`,
		`b`.`ROLE_CODE` AS `ROLE_CODE`,
		`b`.`AUTH_DESC` AS `AUTH_DESC`,
		`c`.`NODE_CODE` AS `node_code`,
		`c`.`NODE_NAME` AS `Node_Name`,
		`c`.`NODE_TYPE` AS `Node_Type`,
		`c`.`OPT_TYPE` AS `NODE_OPT_TYPE`,
		`c`.`OPT_PARAM` AS `Opt_Param`,
		`a`.`CREATE_TIME` AS `CREATE_TIME`,
		`a`.`promise_Time` AS `Promise_Time`,
		`a`.`time_Limit` AS `TIME_LIMIT`,
		`c`.`OPT_CODE` AS `OPT_CODE`,
		`c`.`Expire_Opt` AS `Expire_Opt`,
		`c`.`STAGE_CODE` AS `STAGE_CODE`,
		`a`.`last_update_user` AS `last_update_user`,
		`a`.`last_update_time` AS `LAST_UPDATE_TIME`,
		`w`.`INST_STATE` AS `inst_state`
	FROM
		(
			(
				(
					`wf_node_instance` `a`
					JOIN `wf_flow_instance` `w` ON (
						(
							`a`.`FLOW_INST_ID` = `w`.`FLOW_INST_ID`
						)
					)
				)
				JOIN `wf_action_task` `b` ON (
					(
						`a`.`NODE_INST_ID` = `b`.`NODE_INST_ID`
					)
				)
			)
			JOIN `wf_node` `c` ON (
				(
					`a`.`NODE_ID` = `c`.`NODE_ID`
				)
			)
		)
	WHERE
		(
			(`a`.`NODE_STATE` = 'N')
			AND (`w`.`INST_STATE` = 'N')
			AND (`a`.`TASK_ASSIGNED` = 'T')
			AND (`b`.`IS_VALID` = 'T')
			AND (`b`.`TASK_STATE` = 'A')
			AND (
				isnull(`b`.`EXPIRE_TIME`)
				OR (`b`.`EXPIRE_TIME` > now())
			)
		)
	UNION ALL
		SELECT
			`a`.`FLOW_INST_ID` AS `FLOW_INST_ID`,
			`w`.`FLOW_CODE` AS `FLOW_CODE`,
			`w`.`VERSION` AS `version`,
			`w`.`FLOW_Opt_Name` AS `FLOW_OPT_NAME`,
			`w`.`FLOW_Opt_Tag` AS `FLOW_OPT_TAG`,
			`a`.`NODE_INST_ID` AS `NODE_INST_ID`,
			`b`.`UNITCODE` AS `UnitCode`,
			`b`.`USERCODE` AS `usercode`,
			`c`.`ROLE_TYPE` AS `ROLE_TYPE`,
			`c`.`ROLE_CODE` AS `ROLE_CODE`,
			'系统指定' AS `AUTHDESC`,
			`c`.`NODE_CODE` AS `node_code`,
			`c`.`NODE_NAME` AS `Node_Name`,
			`c`.`NODE_TYPE` AS `Node_Type`,
			`c`.`OPT_TYPE` AS `NODEOPTTYPE`,
			`c`.`OPT_PARAM` AS `Opt_Param`,
			`a`.`CREATE_TIME` AS `CREATE_TIME`,
			`a`.`promise_Time` AS `Promise_Time`,
			`a`.`time_Limit` AS `TIME_LIMIT`,
			`c`.`OPT_CODE` AS `OPT_CODE`,
			`c`.`Expire_Opt` AS `Expire_Opt`,
			`c`.`STAGE_CODE` AS `STAGE_CODE`,
			`a`.`last_update_user` AS `last_update_user`,
			`a`.`last_update_time` AS `LAST_UPDATE_TIME`,
			`w`.`INST_STATE` AS `inst_state`
		FROM
			(
				(
					(
						`wf_node_instance` `a`
						JOIN `wf_flow_instance` `w` ON (
							(
								`a`.`FLOW_INST_ID` = `w`.`FLOW_INST_ID`
							)
						)
					)
					JOIN `wf_node` `c` ON (
						(
							`a`.`NODE_ID` = `c`.`NODE_ID`
						)
					)
				)
				JOIN `f_userunit` `b`
			)
		WHERE
			(
				(`a`.`NODE_STATE` = 'N')
				AND (`w`.`INST_STATE` = 'N')
				AND (`a`.`TASK_ASSIGNED` = 'D')
				AND (
					isnull(`a`.`UNIT_CODE`)
					OR (
						`a`.`UNIT_CODE` = `b`.`UNITCODE`
					)
				)
				AND (
					(
						(`c`.`ROLE_TYPE` = 'gw')
						AND (
							`c`.`ROLE_CODE` = `b`.`UserStation`
						)
					)
					OR (
						(`c`.`ROLE_TYPE` = 'xz')
						AND (
							`c`.`ROLE_CODE` = `b`.`UserRank`
						)
					)
				)
			);

create or replace view V_USER_TASK_LIST as
SELECT
	`a`.`FLOW_INST_ID` AS `FLOW_INST_ID`,
	`a`.`FLOW_CODE` AS `FLOW_CODE`,
	`a`.`version` AS `version`,
	`a`.`FLOW_OPT_NAME` AS `FLOW_OPT_NAME`,
	`a`.`FLOW_OPT_TAG` AS `FLOW_OPT_TAG`,
	`a`.`NODE_INST_ID` AS `NODE_INST_ID`,
	`a`.`UnitCode` AS `UnitCode`,
	`a`.`user_code` AS `user_code`,
	`a`.`ROLE_TYPE` AS `ROLE_TYPE`,
	`a`.`ROLE_CODE` AS `ROLE_CODE`,
	`a`.`AUTHDESC` AS `AUTHDESC`,
	`a`.`node_code` AS `node_code`,
	`a`.`Node_Name` AS `Node_Name`,
	`a`.`Node_Type` AS `Node_Type`,
	`a`.`NODEOPTTYPE` AS `NODEOPTTYPE`,
	`a`.`Opt_Param` AS `Opt_Param`,
	`a`.`CREATE_TIME` AS `CREATE_TIME`,
	`a`.`Promise_Time` AS `promise_time`,
	`a`.`TIME_LIMIT` AS `time_limit`,
	`a`.`OPT_CODE` AS `OPT_CODE`,
	`a`.`Expire_Opt` AS `Expire_Opt`,
	`a`.`STAGE_CODE` AS `STAGE_CODE`,
	NULL AS `GRANTOR`,
	`a`.`last_update_user` AS `last_update_user`,
	`a`.`LAST_UPDATE_TIME` AS `LAST_UPDATE_TIME`,
	`a`.`inst_state` AS `inst_state`
FROM
	`v_inner_user_task_list` `a`
UNION
	SELECT
		`a`.`FLOW_INST_ID` AS `FLOW_INST_ID`,
		`a`.`FLOW_CODE` AS `FLOW_CODE`,
		`a`.`version` AS `version`,
		`a`.`FLOW_OPT_NAME` AS `FLOW_OPT_NAME`,
		`a`.`FLOW_OPT_TAG` AS `FLOW_OPT_TAG`,
		`a`.`NODE_INST_ID` AS `node_inst_id`,
		`a`.`UnitCode` AS `UnitCode`,
		`b`.`GRANTEE` AS `user_code`,
		`a`.`ROLE_TYPE` AS `ROLE_TYPE`,
		`a`.`ROLE_CODE` AS `ROLE_CODE`,
		`a`.`AUTHDESC` AS `AUTHDESC`,
		`a`.`node_code` AS `node_code`,
		`a`.`Node_Name` AS `Node_Name`,
		`a`.`Node_Type` AS `Node_Type`,
		`a`.`NODEOPTTYPE` AS `NODEOPTTYPE`,
		`a`.`Opt_Param` AS `Opt_Param`,
		`a`.`CREATE_TIME` AS `CREATE_TIME`,
		`a`.`Promise_Time` AS `promise_time`,
		`a`.`TIME_LIMIT` AS `time_limit`,
		`a`.`OPT_CODE` AS `OPT_CODE`,
		`a`.`Expire_Opt` AS `Expire_Opt`,
		`a`.`STAGE_CODE` AS `STAGE_CODE`,
		`b`.`GRANTOR` AS `GRANTOR`,
		`a`.`last_update_user` AS `last_update_user`,
		`a`.`LAST_UPDATE_TIME` AS `last_update_time`,
		`a`.`inst_state` AS `inst_state`
	FROM
		(
			`v_inner_user_task_list` `a`
			JOIN `wf_role_relegate` `b`
		)
	WHERE
		(
			(`b`.`IS_VALID` = 'T')
			AND (`b`.`RELEGATE_TIME` <= now())
			AND (
				isnull(`b`.`EXPIRE_TIME`)
				OR (`b`.`EXPIRE_TIME` >= now())
			)
			AND (
				`a`.`user_code` = `b`.`GRANTOR`
			)
			AND (
				isnull(`b`.`UNIT_CODE`)
				OR (
					`b`.`UNIT_CODE` = `a`.`UnitCode`
				)
			)
			AND (
				isnull(`b`.`ROLE_TYPE`)
				OR (
					(
						`b`.`ROLE_TYPE` = `a`.`ROLE_TYPE`
					)
					AND (
						isnull(`b`.`ROLE_CODE`)
						OR (
							`b`.`ROLE_CODE` = `a`.`ROLE_CODE`
						)
					)
				)
			)
		);


/**已办**/
create or replace view V_INNER_USER_TASK_LIST_fin as
  SELECT
    `a`.`FLOW_INST_ID` AS `FLOW_INST_ID`,
    `w`.`FLOW_CODE` AS `FLOW_CODE`,
    `w`.`VERSION` AS `version`,
    `w`.`FLOW_Opt_Name` AS `FLOW_OPT_NAME`,
    `w`.`FLOW_Opt_Tag` AS `FLOW_OPT_TAG`,
    `a`.`NODE_INST_ID` AS `NODE_INST_ID`,
    ifnull(
        `a`.`UNIT_CODE`,
        ifnull(`w`.`UNIT_CODE`, '0000000')
    ) AS `UnitCode`,
    `a`.`USER_CODE` AS `user_code`,
    `c`.`ROLE_TYPE` AS `ROLE_TYPE`,
    `c`.`ROLE_CODE` AS `ROLE_CODE`,
    '一般任务' AS `AUTHDESC`,
    `c`.`NODE_CODE` AS `node_code`,
    `c`.`NODE_NAME` AS `Node_Name`,
    `c`.`NODE_TYPE` AS `Node_Type`,
    `c`.`OPT_TYPE` AS `NODEOPTTYPE`,
    `c`.`OPT_PARAM` AS `Opt_Param`,
    `d`.`optdef_url` AS `Opt_Url`,
    `a`.`CREATE_TIME` AS `CREATE_TIME`,
    `a`.`promise_Time` AS `Promise_Time`,
    `a`.`time_Limit` AS `TIME_LIMIT`,
    `c`.`OPT_CODE` AS `OPT_CODE`,
    `c`.`Expire_Opt` AS `Expire_Opt`,
    `c`.`STAGE_CODE` AS `STAGE_CODE`,
    `a`.`last_update_user` AS `last_update_user`,
    `a`.`last_update_time` AS `LAST_UPDATE_TIME`,
    `w`.`INST_STATE` AS `inst_state`
  FROM
    (
        (
            (
                `wf_node_instance` `a`
                JOIN `wf_flow_instance` `w` ON (
                (
                  `a`.`FLOW_INST_ID` = `w`.`FLOW_INST_ID`
                )
                )
              )
            JOIN `wf_node` `c` ON (
            (
              `a`.`NODE_ID` = `c`.`NODE_ID`
            )
            )
          )
        JOIN `f_v_wf_optdef_url_map` `d` ON (
        (
          `c`.`OPT_CODE` = `d`.`opt_code`
        )
        )
    )
  WHERE
    (
      (`a`.`NODE_STATE` = 'C')
      AND (`a`.`TASK_ASSIGNED` = 'S')
    )
  UNION ALL
  SELECT
    `a`.`FLOW_INST_ID` AS `FLOW_INST_ID`,
    `w`.`FLOW_CODE` AS `FLOW_CODE`,
    `w`.`VERSION` AS `version`,
    `w`.`FLOW_Opt_Name` AS `FLOW_OPT_NAME`,
    `w`.`FLOW_Opt_Tag` AS `FLOW_OPT_TAG`,
    `a`.`NODE_INST_ID` AS `NODE_INST_ID`,
    ifnull(
        `a`.`UNIT_CODE`,
        ifnull(`w`.`UNIT_CODE`, '0000000')
    ) AS `UnitCode`,
    `b`.`USER_CODE` AS `user_code`,
    `b`.`ROLE_TYPE` AS `ROLE_TYPE`,
    `b`.`ROLE_CODE` AS `ROLE_CODE`,
    `b`.`AUTH_DESC` AS `AUTH_DESC`,
    `c`.`NODE_CODE` AS `node_code`,
    `c`.`NODE_NAME` AS `Node_Name`,
    `c`.`NODE_TYPE` AS `Node_Type`,
    `c`.`OPT_TYPE` AS `NODEOPTTYPE`,
    `c`.`OPT_PARAM` AS `Opt_Param`,
    `d`.`optdef_url` AS `Opt_Url`,
    `a`.`CREATE_TIME` AS `CREATE_TIME`,
    `a`.`promise_Time` AS `Promise_Time`,
    `a`.`time_Limit` AS `TIME_LIMIT`,
    `c`.`OPT_CODE` AS `OPT_CODE`,
    `c`.`Expire_Opt` AS `Expire_Opt`,
    `c`.`STAGE_CODE` AS `STAGE_CODE`,
    `a`.`last_update_user` AS `last_update_user`,
    `a`.`last_update_time` AS `LAST_UPDATE_TIME`,
    `w`.`INST_STATE` AS `inst_state`
  FROM
    (
        (
            (
                (
                    `wf_node_instance` `a`
                    JOIN `wf_flow_instance` `w` ON (
                    (
                      `a`.`FLOW_INST_ID` = `w`.`FLOW_INST_ID`
                    )
                    )
                  )
                JOIN `wf_action_task` `b` ON (
                (
                  `a`.`NODE_INST_ID` = `b`.`NODE_INST_ID`
                )
                )
              )
            JOIN `wf_node` `c` ON (
            (
              `a`.`NODE_ID` = `c`.`NODE_ID`
            )
            )
          )
        JOIN `f_v_wf_optdef_url_map` `d` ON (
        (
          `c`.`OPT_CODE` = `d`.`opt_code`
        )
        )
    )
  WHERE
    (
      (`a`.`NODE_STATE` = 'C')
      AND (`a`.`TASK_ASSIGNED` = 'T')
      AND (`b`.`IS_VALID` = 'T')
      AND (`b`.`TASK_STATE` = 'A')
      AND (
        isnull(`b`.`EXPIRE_TIME`)
        OR (`b`.`EXPIRE_TIME` > now())
      )
    )
  UNION ALL
  SELECT
    `a`.`FLOW_INST_ID` AS `FLOW_INST_ID`,
    `w`.`FLOW_CODE` AS `FLOW_CODE`,
    `w`.`VERSION` AS `version`,
    `w`.`FLOW_Opt_Name` AS `FLOW_OPT_NAME`,
    `w`.`FLOW_Opt_Tag` AS `FLOW_OPT_TAG`,
    `a`.`NODE_INST_ID` AS `NODE_INST_ID`,
    `b`.`UNIT_CODE` AS `UnitCode`,
    `b`.`USER_CODE` AS `usercode`,
    `c`.`ROLE_TYPE` AS `ROLE_TYPE`,
    `c`.`ROLE_CODE` AS `ROLE_CODE`,
    '系统指定' AS `AUTHDESC`,
    `c`.`NODE_CODE` AS `node_code`,
    `c`.`NODE_NAME` AS `Node_Name`,
    `c`.`NODE_TYPE` AS `Node_Type`,
    `c`.`OPT_TYPE` AS `NODEOPTTYPE`,
    `c`.`OPT_PARAM` AS `Opt_Param`,
    `d`.`optdef_url` AS `Opt_Url`,
    `a`.`CREATE_TIME` AS `CREATE_TIME`,
    `a`.`promise_Time` AS `Promise_Time`,
    `a`.`time_Limit` AS `TIME_LIMIT`,
    `c`.`OPT_CODE` AS `OPT_CODE`,
    `c`.`Expire_Opt` AS `Expire_Opt`,
    `c`.`STAGE_CODE` AS `STAGE_CODE`,
    `a`.`last_update_user` AS `last_update_user`,
    `a`.`last_update_time` AS `LAST_UPDATE_TIME`,
    `w`.`INST_STATE` AS `inst_state`
  FROM
    (
        (
            (
                (
                    `wf_node_instance` `a`
                    JOIN `wf_flow_instance` `w` ON (
                    (
                      `a`.`FLOW_INST_ID` = `w`.`FLOW_INST_ID`
                    )
                    )
                  )
                JOIN `wf_node` `c` ON (
                (
                  `a`.`NODE_ID` = `c`.`NODE_ID`
                )
                )
              )
            JOIN `f_v_wf_optdef_url_map` `d` ON (
            (
              `c`.`OPT_CODE` = `d`.`opt_code`
            )
            )
          )
        JOIN `f_userunit` `b`
    )
  WHERE
    (
      (`a`.`NODE_STATE` = 'C')
      AND (`a`.`TASK_ASSIGNED` = 'D')
      AND (
        isnull(`a`.`UNIT_CODE`)
        OR (
          `a`.`UNIT_CODE` = `b`.`UNIT_CODE`
        )
      )
      AND (
        (
          (`c`.`ROLE_TYPE` = 'gw')
          AND (
            `c`.`ROLE_CODE` = `b`.`User_Station`
          )
        )
        OR (
          (`c`.`ROLE_TYPE` = 'xz')
          AND (
            `c`.`ROLE_CODE` = `b`.`User_Rank`
          )
        )
      )
    )

create or replace view V_USER_TASK_LIST_fin as
  SELECT
    `a`.`FLOW_INST_ID` AS `FLOW_INST_ID`,
    `a`.`FLOW_CODE` AS `FLOW_CODE`,
    `a`.`version` AS `version`,
    `a`.`FLOW_OPT_NAME` AS `FLOW_OPT_NAME`,
    `a`.`FLOW_OPT_TAG` AS `FLOW_OPT_TAG`,
    `a`.`NODE_INST_ID` AS `NODE_INST_ID`,
    `a`.`UnitCode` AS `Unit_Code`,
    `a`.`user_code` AS `user_code`,
    `a`.`ROLE_TYPE` AS `ROLE_TYPE`,
    `a`.`ROLE_CODE` AS `ROLE_CODE`,
    `a`.`AUTHDESC` AS `AUTH_DESC`,
    `a`.`node_code` AS `node_code`,
    `a`.`Node_Name` AS `Node_Name`,
    `a`.`Node_Type` AS `Node_Type`,
    `a`.`NODEOPTTYPE` AS `NODE_OPT_TYPE`,
    `a`.`Opt_Param` AS `Opt_Param`,
    `a`.`Opt_Url` AS `Opt_url`,
    `a`.`CREATE_TIME` AS `CREATE_TIME`,
    `a`.`Promise_Time` AS `promise_time`,
    `a`.`TIME_LIMIT` AS `time_limit`,
    `a`.`OPT_CODE` AS `OPT_CODE`,
    `a`.`Expire_Opt` AS `Expire_Opt`,
    `a`.`STAGE_CODE` AS `STAGE_CODE`,
    NULL AS `GRANTOR`,
    `a`.`last_update_user` AS `last_update_user`,
    `a`.`LAST_UPDATE_TIME` AS `LAST_UPDATE_TIME`,
    `a`.`inst_state` AS `inst_state`
  FROM
    `v_inner_user_task_list_fin` `a`
  UNION
  SELECT
    `a`.`FLOW_INST_ID` AS `FLOW_INST_ID`,
    `a`.`FLOW_CODE` AS `FLOW_CODE`,
    `a`.`version` AS `version`,
    `a`.`FLOW_OPT_NAME` AS `FLOW_OPT_NAME`,
    `a`.`FLOW_OPT_TAG` AS `FLOW_OPT_TAG`,
    `a`.`NODE_INST_ID` AS `node_inst_id`,
    `a`.`UnitCode` AS `Unit_Code`,
    `b`.`GRANTEE` AS `user_code`,
    `a`.`ROLE_TYPE` AS `ROLE_TYPE`,
    `a`.`ROLE_CODE` AS `ROLE_CODE`,
    `a`.`AUTHDESC` AS `AUTH_DESC`,
    `a`.`node_code` AS `node_code`,
    `a`.`Node_Name` AS `Node_Name`,
    `a`.`Node_Type` AS `Node_Type`,
    `a`.`NODEOPTTYPE` AS `NODE_OPT_TYPE`,
    `a`.`Opt_Param` AS `Opt_Param`,
    `a`.`Opt_Url` AS `Opt_url`,
    `a`.`CREATE_TIME` AS `CREATE_TIME`,
    `a`.`Promise_Time` AS `promise_time`,
    `a`.`TIME_LIMIT` AS `time_limit`,
    `a`.`OPT_CODE` AS `OPT_CODE`,
    `a`.`Expire_Opt` AS `Expire_Opt`,
    `a`.`STAGE_CODE` AS `STAGE_CODE`,
    `b`.`GRANTOR` AS `GRANTOR`,
    `a`.`last_update_user` AS `last_update_user`,
    `a`.`LAST_UPDATE_TIME` AS `last_update_time`,
    `a`.`inst_state` AS `inst_state`
  FROM
    (
        `v_inner_user_task_list_fin` `a`
        JOIN `wf_role_relegate` `b`
    )
  WHERE
    (
      (`b`.`IS_VALID` = 'T')
      AND (`b`.`RELEGATE_TIME` <= now())
      AND (
        isnull(`b`.`EXPIRE_TIME`)
        OR (`b`.`EXPIRE_TIME` >= now())
      )
      AND (
        `a`.`user_code` = `b`.`GRANTOR`
      )
      AND (
        isnull(`b`.`UNIT_CODE`)
        OR (
          `b`.`UNIT_CODE` = `a`.`UnitCode`
        )
      )
      AND (
        isnull(`b`.`ROLE_TYPE`)
        OR (
          (
            `b`.`ROLE_TYPE` = `a`.`ROLE_TYPE`
          )
          AND (
            isnull(`b`.`ROLE_CODE`)
            OR (
              `b`.`ROLE_CODE` = `a`.`ROLE_CODE`
            )
          )
        )
      )
    )