package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);
    /**
     * 新增员工
     */
    void save(EmployeeDTO employeeDTO);
//分页查询
    PageResult pageQuery(EmployeePageQueryDTO edto);

    void startOrStop(Integer status, Long id);
    /**
     * 通过id查询员工信息
     * @param id
     * @return
     */
    Employee getById(Long id);
//更新员工信息
    void update(EmployeeDTO employeeDTO);
}
