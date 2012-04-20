package test;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;

class Blog {
	private Author writer;
	private List entries = new ArrayList();

	public Blog(Author writer) {
		this.writer = writer;
	}

	public void add(Entry entry) {
		entries.add(entry);
	}

	public List getContent() {
		return entries;
	}
}

class Author {
	private String name;

	public Author(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}

class Entry {
	private String title, description;

	public Entry(String title, String description) {
		this.title = title;
		this.description = description;
	}
}

class AuthorConverter implements SingleValueConverter {

    public String toString(Object obj) {
            return ((Author) obj).getName();
    }

    public Object fromString(String name) {
            return new Author(name);
    }

    public boolean canConvert(Class type) {
            return type.equals(Author.class);
    }

}

public class Test1 {
	public static void main(String[] args) {

		 Blog teamBlog = new Blog(new Author("Guilherme Silveira"));
         teamBlog.add(new Entry("first","My first blog entry."));
         teamBlog.add(new Entry("tutorial",
                 "Today we have developed a nice alias tutorial. Tell your friends! NOW!"));

         XStream xstream = new XStream();
         xstream.alias("blog", Blog.class);
         xstream.alias("entry", Entry.class);

         xstream.addImplicitCollection(Blog.class, "entries");

         xstream.aliasField("author", Blog.class, "writer");
         
         xstream.useAttributeFor(Blog.class, "writer");
         xstream.registerConverter(new AuthorConverter());

         System.out.println(xstream.toXML(teamBlog));
         
	}
}
