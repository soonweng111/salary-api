package the.mini.project.salaryapi.Utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import the.mini.project.salaryapi.utility.PageBasedOnOffset;

public class PageBasedOnOffsetTest {

    private final PageBasedOnOffset page = new PageBasedOnOffset(0,10, Sort.unsorted());

    @Test
    public void getPageNumberTest(){
        Assertions.assertEquals(0, page.getPageNumber());
    }

    @Test
    public void getPageSizeTest(){
        Assertions.assertEquals(10, page.getPageSize());
    }

    @Test
    public void getIntOffSetTest(){
        Assertions.assertInstanceOf(Integer.class, page.getIntOffset());
    }

    @Test
    public void getSortTest(){
        Assertions.assertEquals(Sort.unsorted(), page.getSort());
    }

    @Test
    public void nextTest(){
        Assertions.assertEquals(10, page.next().getOffset());
    }

    @Test
    public void previousTest(){
        Assertions.assertEquals(0, page.previous().getOffset());
    }

    @Test
    public void previousOrFirstTest(){
        Assertions.assertEquals(0, page.previousOrFirst().getOffset());
    }

    @Test
    public void firstTest(){
        Assertions.assertEquals(10, page.first().getPageSize());
    }

    @Test
    public void withPageTest(){
        Assertions.assertNull(page.withPage(1));
    }

    @Test
    public void hasPreviousTest(){
        Assertions.assertFalse(page.hasPrevious());
    }

    @Test
    public void equalsTrueTest(){
        Assertions.assertEquals(page, page);
    }

    @Test
    public void equalsFalseTest(){
        PageBasedOnOffset page2 = new PageBasedOnOffset(10,10, Sort.unsorted());
        Assertions.assertNotEquals(page, page2);
    }

    @Test
    public void hashCodeTest(){
        Assertions.assertEquals(875319, page.hashCode());
    }

    @Test
    public void toStringTest(){
        Assertions.assertTrue(page.toString().contains("limit=10,offset=0,sort=UNSORTED"));
    }
}
