.class public Fac
.super java/lang/Object

.method public <init>()V
	aload_0
	invokespecial java/lang/Object.<init>()V
	return
.end method

.method public compFac(I)I
	.limit stack 3
	.limit locals 6
	iload 1
	iconst_1
	if_icmplt ifBlock1
	iload 1
	iconst_1
	isub
	istore 2
	aload_0
	iload 2
	invokevirtual Fac.compFac(I)I
	istore 3
	iload 1
	iload 3
	imul
	istore 4
	goto endif1
ifBlock1:
	iconst_1
	istore 4
endif1:
	iload 4
	ireturn
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 3
	.limit locals 6
	new Fac
	dup
	astore 1
	aload 1
	invokespecial Fac.<init>()V
	aload 1
	bipush 10
	invokevirtual Fac.compFac(I)I
	istore 3
	iload 3
	invokestatic io.println(I)V
	return
.end method