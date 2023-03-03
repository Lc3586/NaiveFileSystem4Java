package top.lctr.naive.file.system.common;

import project.extension.standard.exception.BusinessException;

/**
 * 异常类扩展方法
 *
 * @author LCTR
 * @date 2023-02-03
 */
public class ExceptionExtension {
    /**
     * 输出
     *
     * @param exception 异常信息
     */
    public static void output(Throwable exception) {
        if (exception.getClass()
                     .equals(BusinessException.class)) {
            BusinessException moduleException = (BusinessException) exception;
            if (moduleException.getInnerException() != null && moduleException.getInnerException()
                                                                              .getClass()
                                                                              .equals(BusinessException.class)) {
                output(moduleException.getInnerException());
            }
        }

        System.out.println("\r\n" + exception.getMessage());
    }
}
