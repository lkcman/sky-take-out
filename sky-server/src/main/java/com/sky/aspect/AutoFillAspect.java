package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Component
@Aspect
@Slf4j
public class AutoFillAspect {
//    /**
//     * 定义切入点
//     * execution(* com.sky.mapper.*.*(..))&&需要时就加
//     */
//    @Pointcut("@annotation(com.sky.annotation.AutoFill)")
//    public void autoFillPointCut() {
//
//    }

//    /**
//     * 自动填充
//     * @param joinPoint
//     */
//  @Before("autoFillPointCut()")
//    public void autoFillBefore(JoinPoint joinPoint) {
////获取参数
//      Object[] args = joinPoint.getArgs();
//      for (Object arg : args) {
//          //实体类参数需要处理
//          //获取包名
//          //获取参数的字节码
//          Class<?> cls = arg.getClass();
//           String pkgName= cls.getPackage().getName();
//           if ("com.sky.eneity".equals(pkgName)) {
//
//               //获取操作类型
//               MethodSignature signature = (MethodSignature)joinPoint.getSignature();
//               Method method = signature.getMethod();
//               AutoFill autoFill = method.getAnnotation(AutoFill.class);
//               OperationType operationType = autoFill.value();
//               System.out.println(operationType);
//               try {
//                   Method setCreateTime = cls.getDeclaredMethod("setCreateTime", LocalDateTime.class);
//                   Method setUpdateTime = cls.getDeclaredMethod("setUpdateTime", LocalDateTime.class);
//                   Method setCreateUser = cls.getDeclaredMethod("setCreateUser ", Long.class);
//                   Method setUpdataUser = cls.getDeclaredMethod("setUpdataUser", Long.class);
//
//                   LocalDateTime now = LocalDateTime.now();
//                   Long logincurrentId = BaseContext.getCurrentId();
//                   //方法判断
//                   switch (operationType){
//                       case INSERT:
//                           setCreateTime.invoke(arg,now);
//                           setUpdateTime.invoke(arg,now);
//                           setCreateUser.invoke(arg,logincurrentId);
//                           setUpdataUser.invoke(arg,logincurrentId);
//                           break;//insert(4,set)
//                       case UPDATE:
//                           setUpdateTime.invoke(arg,now);
//                           setUpdataUser.invoke(arg,logincurrentId);
//                           break;//updata(2,set)
//                   }
//
//               } catch (Exception e) {
//                   throw new RuntimeException(e);
//               }
//
//               //通过参数获取set方法
//               //获取当前系统时间，当前登录id
//               //调用方法
//           }
//      }
//
//  }
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知，在通知中进行公共字段的赋值
     */

      @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段自动填充...");

        //获取到当前被拦截的方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType = autoFill.value();//获得数据库操作类型

        //获取到当前被拦截的方法的参数--实体对象
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }

        Object entity = args[0];

        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据当前不同的操作类型，为对应的属性通过反射来赋值
        if(operationType == OperationType.INSERT){
            //为4个公共字段赋值
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(operationType == OperationType.UPDATE){
            //为2个公共字段赋值
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
