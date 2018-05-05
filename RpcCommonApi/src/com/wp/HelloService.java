package com.wp;

import com.wp.pojo.Person;

public interface HelloService {
	public Person sayHello(Person person, String message);
	
	public Person sayHello(Person person);
	
	public Person sayHello(String message);
}
