package com.kakaopay.pete.agent;

import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class TimeLoggingTransfomer implements ClassFileTransformer {

    private static Logger log = LoggerFactory.getLogger(TimeLoggingTransfomer.class);

    /** The internal form class name of the class to transform */
    private Class<?> targetClass;
    /** The class loader of the class we want to transform */
    private ClassLoader targetClassLoader;

    public TimeLoggingTransfomer(Class<?> targetClass, ClassLoader targetClassLoader) {
        this.targetClass = targetClass;
        this.targetClassLoader = targetClassLoader;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        byte[] byteCode = classfileBuffer;

        String finalTargetClassName = this.targetClass.getName().replaceAll("\\.", "/"); //replace . with /
        if (!className.equals(finalTargetClassName)) {
            return byteCode;
        }

        if (loader == null && targetClassLoader != null) {
            return byteCode;
        }
        if (loader != null && !loader.equals(targetClassLoader)) {
            return byteCode;
        }

        log.info("[Agent] Transforming class " + targetClass.getName());

        try {
            ClassPool cp = ClassPool.getDefault();
            if (targetClassLoader != null)
                cp.appendClassPath(new LoaderClassPath(targetClassLoader));
            CtClass cc = cp.get(targetClass.getName());
            CtMethod m = cc.getDeclaredMethod("indexOf");
            m.addLocalVariable("startTime", CtClass.longType);
            StringBuilder startBlock = new StringBuilder();
            startBlock.append("startTime = System.currentTimeMillis();");
            startBlock.append("System.out.println(\"[Agent] ArrayList.indexOf startTime:\" + startTime);");
            m.insertBefore(startBlock.toString());

            StringBuilder endBlock = new StringBuilder();

            m.addLocalVariable("endTime", CtClass.longType);
            m.addLocalVariable("opTime", CtClass.longType);
            endBlock.append("endTime = System.currentTimeMillis();");
            endBlock.append("opTime = endTime-startTime;");

            endBlock.append("System.out.println(\"[Agent] ArrayList.indexOf performance: endTime=\" + endTime + \", opTime=\" + opTime + \" seconds!\");");

            m.insertAfter(endBlock.toString());

            byteCode = cc.toBytecode();
            cc.detach();
            log.info("success to write bytecode");
        } catch (Exception e) {
            log.error("error transform class.", e);
        }

        return byteCode;
    }
}
