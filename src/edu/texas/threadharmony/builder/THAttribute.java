package edu.texas.threadharmony.builder;

import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Visitor;

public class THAttribute extends Attribute {

	protected THAttribute(byte tag, int name_index, int length,
			ConstantPool constant_pool) {
		super(tag, name_index, length, constant_pool);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub

	}

	@Override
	public Attribute copy(ConstantPool _constant_pool) {
		// TODO Auto-generated method stub
		return null;
	}

}
