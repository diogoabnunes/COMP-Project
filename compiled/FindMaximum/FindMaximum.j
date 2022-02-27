.class public FindMaximum
.super java/lang/Object
.field private test_arr [I
.field private a I

.method public <init>()V
	aload_0
	invokespecial java/lang/Object.<init>()V
	return
.end method

.method public find_maximum([I)I
	.limit stack 2
	.limit locals 8
	iconst_1
	istore 2
	iconst_0
	istore 3
	aload 1
	iload 3
	iaload
	istore 4
Loop1:
	aload 1
	arraylength
	istore 5
	iload 2
	iload 5
	if_icmplt Body1
	goto EndLoop1
Body1:
	iload 2
	istore 6
	aload 1
	iload 6
	iaload
	istore 7
	iload 4
	iload 7
	if_icmplt ifBlock1
	goto endif1
ifBlock1:
	iload 7
	istore 4
endif1:
	iinc 2 1
	goto Loop1
EndLoop1:
	iload 4
	ireturn
.end method

.method public build_test_arr()I
	.limit stack 10
	.limit locals 16
	aload_0
	iconst_1
	putfield FindMaximum/a I
	iconst_5
	newarray int
	astore 1
	aload_0
	aload 1
	putfield FindMaximum/test_arr [I
	aload_0
	getfield FindMaximum/test_arr [I
	astore 3
	iconst_0
	istore 4
	aload 3
	iload 4
	bipush 14
	iastore
	aload_0
	getfield FindMaximum/test_arr [I
	astore 5
	iconst_1
	istore 6
	aload 5
	iload 6
	bipush 28
	iastore
	aload_0
	getfield FindMaximum/test_arr [I
	astore 7
	iconst_2
	istore 8
	aload 7
	iload 8
	iconst_0
	iastore
	aload_0
	getfield FindMaximum/test_arr [I
	astore 9
	iconst_3
	istore 10
	aload 9
	iload 10
	iconst_0
	iconst_5
	isub
	iastore
	aload_0
	getfield FindMaximum/test_arr [I
	astore 11
	iconst_4
	istore 12
	aload 11
	iload 12
	bipush 12
	iastore
	iconst_0
	ireturn
.end method

.method public get_array()[I
	.limit stack 2
	.limit locals 4
	aload_0
	getfield FindMaximum/test_arr [I
	astore 1
	aload 1
	areturn
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 6
	.limit locals 8
	new FindMaximum
	dup
	astore 1
	aload 1
	invokespecial FindMaximum.<init>()V
	aload 1
	astore 3
	aload 3
	invokevirtual FindMaximum.build_test_arr()I
	aload 3
	invokevirtual FindMaximum.get_array()[I
	astore 4
	aload 3
	aload 4
	invokevirtual FindMaximum.find_maximum([I)I
	istore 5
	iload 5
	invokestatic ioPlus.printResult(I)V
	return
.end method