package com.wp.util;

import com.wp.HelloService;
import com.wp.pojo.Person;

public class HelloServiceImpl implements HelloService{

	@Override
	public Person sayHello(Person person, String message) {
		//System.out.println("request person = "+person.toString());
		//System.out.println("request message = "+message);
		
		/*try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		Person p = new Person(person.getName()+message,person.getAge()+1);
		
		return p;
	}

	@Override
	public Person sayHello(Person person) {
		System.out.println("request person = "+person.toString());
		
		Person p = new Person(person.getName(),person.getAge()+1);
		
		return p;
	}

	@Override
	public Person sayHello(String message) {
		System.out.println("request message = "+message);
		
		Person p = new Person(message,6);
		
		return p;
	}

}
