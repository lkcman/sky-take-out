package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
       password= DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    @Override
    public void save( EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //对象属性拷贝，把EmployeeDto的属性赋值给实体类employee
       BeanUtils.copyProperties(employeeDTO,employee);
        //设置账号的状态，默认正常状态 1表示正常 0表示锁定
        employee.setStatus(StatusConstant.ENABLE);
        //设置密码，默认密码123456
        String s = DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes());
        employee.setPassword(s);
        //设置当前记录的创建时间和修改时间
        LocalDateTime now = LocalDateTime.now();
        employee.setCreateTime(now);
        employee.setUpdateTime(now);
        //设置当前记录创建人id和修改人id
        employee.setCreateUser(10L);//java默认int,
        employee.setUpdateUser(10L);
        //登录成功的用户id存入线程中，把用户存入token的载荷中
        Long loginCurrentId = BaseContext.getCurrentId();
        log.info("员工登录的id:{}",loginCurrentId);
        employee.setCreateUser(loginCurrentId);
        employee.setUpdateUser(loginCurrentId);
        //利用mapper插入
        employeeMapper.insert(employee);
    }


    /**
     * 根据名字分页查询
     * @param edto
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO edto) {

        PageHelper.startPage(edto.getPage(), edto.getPageSize());

        Page<Employee> employees = employeeMapper.pageQuery(edto);
        long total = employees.getTotal();
        List<Employee> result = employees.getResult();

        return new PageResult(total,result);

    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Employee updataemployee = new Employee();

         updataemployee.setId(id);

         updataemployee.setStatus(status);
         updataemployee.setUpdateTime(LocalDateTime.now());
         updataemployee.setUpdateUser(BaseContext.getCurrentId());
         employeeMapper.update(updataemployee);
    }


}
