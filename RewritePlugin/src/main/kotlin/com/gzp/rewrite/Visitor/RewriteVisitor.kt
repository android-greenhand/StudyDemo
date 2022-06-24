package com.gzp.rewrite.Visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class RewriteVisitor(api: Int, classVisitor: ClassVisitor) : ClassVisitor(api, classVisitor) {

    override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
        super.visit(version, access, name, signature, superName, interfaces)
        println("first is success")
    }


    override fun visitEnd() {
        super.visitEnd()
        //insertOnResumeWithLog(this.cv as ClassWriter)
        // insertGetResourcesMethod(this.cv as ClassWriter)
        insertOnAppCompatActivityResumeWithLog(this.cv as ClassWriter)
    }

    fun insertGetResourcesMethod(cw: ClassWriter) {
        val mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getResources", "()Landroid/content/res/Resources;", null, null)
        mv.visitCode()
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "android/app/Activity", "getResources", "()Landroid/content/res/Resources;")
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMaxs(2, 1)
        mv.visitEnd()
    }


    fun insertOnResumeWithLog(cw: ClassWriter) {
        val mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "onResume", "()V", null, null)
        mv.visitCode()
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,"androidx/appcompat/app/AppCompatActivity","onResume","()V",false)
        mv.visitLdcInsn("gzp")
        mv.visitLdcInsn("asm success !!!")
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I",false)
        mv.visitInsn(Opcodes.POP)
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(2, 1)
        mv.visitEnd()
    }

    fun insertOnAppCompatActivityResumeWithLog(cw: ClassWriter) {
        val mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "onResume", "()V", null, null)
        mv.visitCode()
        mv.visitVarInsn(Opcodes.ALOAD, 0)

        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,"androidx/fragment/app/FragmentActivity","onResume","()V",false)
        mv.visitLdcInsn("gzp")
        mv.visitLdcInsn("asm success !!!")
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I",false)
        mv.visitInsn(Opcodes.POP)
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(2, 1)
        mv.visitEnd()
    }
}