.class public Simple
.super java/lang/Object
.field private x I

.method public <init>()V
	aload_0
	invokespecial java/lang/Object.<init>()V
	return
.end method

.method public add(II)I
	.limit stack 3
	.limit locals 6
	aload_0
	invokevirtual Simple.constInstr()I
	istore 3
	iload 1
	iload 3
	iadd
	istore 4
	iload 4
	ireturn
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 4
	.limit locals 9
	bipush 20
	istore 1
	bipush 10
	istore 2
	new Simple
	dup
	astore 3
	aload 3
	invokespecial Simple.<init>()V
	aload 3
	astore 5
	aload 5
	iload 1
	iload 2
	invokevirtual Simple.add(II)I
	istore 6
	iload 6
	invokestatic io.println(I)V
	return
.end method

.method public constInstr()I
	.limit stack 1
	.limit locals 2
	iconst_0
	istore 1
	iconst_4
	istore 1
	bipush 8
	istore 1
	bipush 14
	istore 1
	sipush 250
	istore 1
	sipush 400
	istore 1
	sipush 1000
	istore 1
	ldc 100474650
	istore 1
	bipush 10
	istore 1
	iload 1
	ireturn
.end method