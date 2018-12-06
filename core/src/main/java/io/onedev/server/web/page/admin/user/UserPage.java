package io.onedev.server.web.page.admin.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import io.onedev.server.OneDev;
import io.onedev.server.manager.UserManager;
import io.onedev.server.model.User;
import io.onedev.server.util.facade.UserFacade;
import io.onedev.server.web.component.sidebar.SideBar;
import io.onedev.server.web.component.tabbable.PageTab;
import io.onedev.server.web.component.tabbable.Tab;
import io.onedev.server.web.component.user.avatar.UserAvatar;
import io.onedev.server.web.page.admin.AdministrationPage;
import io.onedev.server.util.userident.UserIdent;
import io.onedev.server.web.util.model.EntityModel;

@SuppressWarnings("serial")
public abstract class UserPage extends AdministrationPage {
	
	private static final String PARAM_USER = "user";
	
	protected final IModel<User> userModel;
	
	public UserPage(PageParameters params) {
		User user = OneDev.getInstance(UserManager.class).load(params.get(PARAM_USER).toLong());
		userModel = new EntityModel<User>(user);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		add(new SideBar("userSidebar", "user.miniSidebar") {

			@Override
			protected Component newHead(String componentId) {
				Fragment fragment = new Fragment(componentId, "sidebarHeadFrag", UserPage.this);
				User user = userModel.getObject();
				fragment.add(new UserAvatar("avatar", UserIdent.of(UserFacade.of(user))).add(AttributeAppender.append("title", user.getDisplayName())));
				fragment.add(new Label("name", user.getDisplayName()));
				return fragment;
			}
			
			@Override
			protected List<? extends Tab> newTabs() {
				List<PageTab> tabs = new ArrayList<>();
				
				tabs.add(new UserTab("Profile", "fa fa-fw fa-list-alt", UserProfilePage.class));
				tabs.add(new UserTab("Edit Avatar", "fa fa-fw fa-picture-o", UserAvatarPage.class));
					
				tabs.add(new UserTab("Change Password", "fa fa-fw fa-key", UserPasswordPage.class));
				tabs.add(new UserTab("Access Token", "fa fa-fw fa-ticket", UserTokenPage.class));
				tabs.add(new UserTab("Belonging Groups", "fa fa-fw fa-group", UserMembershipsPage.class));
				tabs.add(new UserTab("Authorized Projects", "fa fa-fw fa-ext fa-repo", UserAuthorizationsPage.class));
				
				return tabs;
			}
			
		});
	}

	@Override
	protected void onDetach() {
		userModel.detach();
		
		super.onDetach();
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new UserCssResourceReference()));
	}
	
	public User getUser() {
		return userModel.getObject();
	}
	
	public static PageParameters paramsOf(User user) {
		PageParameters params = new PageParameters();
		params.add(PARAM_USER, user.getId());
		return params;
	}

}