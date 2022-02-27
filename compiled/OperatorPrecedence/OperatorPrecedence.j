.class public NestedLoop
.super java/lang/Object

.method public <init>()V
	aload_0
	invokespecial java/lang/Object.<init>()V
	return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 2
	.limit locals 8
	iconst_1
	iconst_3
	imul
	istore 1
	iconst_4
	iconst_5
	idiv
	istore 2
	iconst_2
	iconst_5
	isub
	istore 3
	iload 2
	iload 3
	imul
	istore 4
	iload 1
	iload 4
	isub
	istore 5
	iload 5
	invokestatic io.println(I)V
	return
.end method