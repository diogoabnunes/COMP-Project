.class public WhileAndIF
.super java/lang/Object

.method public <init>()V
	aload_0
	invokespecial java/lang/Object.<init>()V
	return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 4
	.limit locals 12
	bipush 20
	istore 1
	bipush 10
	istore 2
	bipush 10
	newarray int
	astore 3
	iload 1
	iload 2
	if_icmplt ifBlock1
	iload 2
	iconst_1
	isub
	istore 5
	goto endif1
ifBlock1:
	iload 1
	iconst_1
	isub
	istore 5
Loop1:
endif1:
	iconst_0
	iconst_1
	isub
	istore 6
	iload 6
	iload 5
	if_icmplt Body1
	goto EndLoop1
Body1:
	aload 3
	iload 5
	iload 1
	iload 2
	isub
	iastore
	iload 5
	iconst_1
	isub
	istore 5
	iload 1
	iconst_1
	isub
	istore 1
	iload 2
	iconst_1
	isub
	istore 2
	goto Loop1
EndLoop1:
	iconst_0
	istore 5
Loop2:
	aload 3
	arraylength
	istore 7
	iload 5
	iload 7
	if_icmplt Body2
	goto EndLoop2
Body2:
	iload 5
	istore 8
	aload 3
	iload 8
	iaload
	istore 9
	iload 9
	invokestatic io.println(I)V
	iinc 5 1
	goto Loop2
EndLoop2:
	return
.end method