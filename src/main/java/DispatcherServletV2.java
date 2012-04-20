

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

public class DispatcherServletV2 extends DispatcherServlet {

	@Override
	protected LocaleContext buildLocaleContext(HttpServletRequest request) {
		System.out.println("1�� ����");
		return super.buildLocaleContext(request);
	}

	@Override
	protected HttpServletRequest checkMultipart(HttpServletRequest request)
			throws MultipartException {
		System.out.println("2�� ����");
		return super.checkMultipart(request);
	}

	@Override
	protected void cleanupMultipart(HttpServletRequest request) {
		System.out.println("3�� ����");
		super.cleanupMultipart(request);
	}

	@Override
	protected Object createDefaultStrategy(ApplicationContext context,
			Class<?> clazz) {
		System.out.println("4�� ����");
		return super.createDefaultStrategy(context, clazz);
	}

	@Override
	protected void doDispatch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println("5�� ����");
		super.doDispatch(request, response);
	}

	@Override
	protected void doService(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println("6�� ����");
		super.doService(request, response);
	}

	@Override
	protected <T> List<T> getDefaultStrategies(ApplicationContext context,
			Class<T> strategyInterface) {
		System.out.println("7�� ����");
		return super.getDefaultStrategies(context, strategyInterface);
	}

	@Override
	protected <T> T getDefaultStrategy(ApplicationContext context,
			Class<T> strategyInterface) {
		System.out.println("8�� ����");
		return super.getDefaultStrategy(context, strategyInterface);
	}

	@Override
	protected String getDefaultViewName(HttpServletRequest request)
			throws Exception {
		System.out.println("9�� ����");
		return super.getDefaultViewName(request);
	}

	@Override
	protected HandlerExecutionChain getHandler(HttpServletRequest request,
			boolean cache) throws Exception {
		System.out.println("10�� ����");
		return super.getHandler(request, cache);
	}

	@Override
	protected HandlerExecutionChain getHandler(HttpServletRequest request)
			throws Exception {
		System.out.println("11�� ����");
		return super.getHandler(request);
	}

	@Override
	protected HandlerAdapter getHandlerAdapter(Object handler)
			throws ServletException {
		System.out.println("12�� ����");
		return super.getHandlerAdapter(handler);
	}

	@Override
	protected void initStrategies(ApplicationContext context) {
		System.out.println("13�� ����");
		super.initStrategies(context);
	}

	@Override
	protected void noHandlerFound(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println("14�� ����");
		super.noHandlerFound(request, response);
	}

	@Override
	protected void onRefresh(ApplicationContext context) {
		System.out.println("15�� ����");
		super.onRefresh(context);
	}

	@Override
	protected ModelAndView processHandlerException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		System.out.println("16�� ����");
		return super.processHandlerException(request, response, handler, ex);
	}

	@Override
	protected void render(ModelAndView mv, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println("17�� ����");
		super.render(mv, request, response);
	}

	@Override
	protected View resolveViewName(String viewName, Map<String, Object> model,
			Locale locale, HttpServletRequest request) throws Exception {
		System.out.println("18�� ����");
		return super.resolveViewName(viewName, model, locale, request);
	}

	@Override
	public void setCleanupAfterInclude(boolean cleanupAfterInclude) {
		System.out.println("19�� ����");
		super.setCleanupAfterInclude(cleanupAfterInclude);
	}

	@Override
	public void setDetectAllHandlerAdapters(boolean detectAllHandlerAdapters) {
		System.out.println("20�� ����");
		super.setDetectAllHandlerAdapters(detectAllHandlerAdapters);
	}

	@Override
	public void setDetectAllHandlerExceptionResolvers(
			boolean detectAllHandlerExceptionResolvers) {
		System.out.println("21�� ����");
		super.setDetectAllHandlerExceptionResolvers(detectAllHandlerExceptionResolvers);
	}

	@Override
	public void setDetectAllHandlerMappings(boolean detectAllHandlerMappings) {
		System.out.println("22�� ����");
		super.setDetectAllHandlerMappings(detectAllHandlerMappings);
	}

	@Override
	public void setDetectAllViewResolvers(boolean detectAllViewResolvers) {
		System.out.println("23�� ����");
		super.setDetectAllViewResolvers(detectAllViewResolvers);
	}

}
