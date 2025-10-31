package todo.todo.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityContexts {

    private static final ThreadLocal<SecurityContexts> context = new ThreadLocal<>();

    private Object data;

    public static SecurityContexts getContext(){
        return context.get();
    }

    public static void newContext() {
        SecurityContexts contexts = new SecurityContexts();
        SecurityContexts.context.set(contexts);
    }
}