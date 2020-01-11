/* 
* Generated by
* 
*      _____ _          __  __      _     _
*     / ____| |        / _|/ _|    | |   | |
*    | (___ | | ____ _| |_| |_ ___ | | __| | ___ _ __
*     \___ \| |/ / _` |  _|  _/ _ \| |/ _` |/ _ \ '__|
*     ____) |   < (_| | | | || (_) | | (_| |  __/ |
*    |_____/|_|\_\__,_|_| |_| \___/|_|\__,_|\___|_|
*
* The code generator that works in many programming languages
*
*			https://www.skaffolder.com
*
*
* You can generate the code from the command-line
*       https://npmjs.com/package/skaffolder-cli
*
*       npm install -g skaffodler-cli
*
*   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *
*
* To remove this comment please upgrade your plan here: 
*      https://app.skaffolder.com/#!/upgrade
*
* Or get up to 70% discount sharing your unique link:
*       https://app.skaffolder.com/#!/register?friend=5e1982d8a4f4b55911b4d29f
*
* You will get 10% discount for each one of your friends
* 
*/
package com.ccim.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ccim.db.ccim_db.service.SecurityService;

public class CommonAuthenticationTokenFilter extends OncePerRequestFilter {
	
	@Autowired
	private SecurityService securityService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    	
    	String token = request.getHeader("Authorization");

        if ( token != null && !token.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
        	token = token.replace("Bearer ", "");
        	try{
        		
        		String jsonUserDetails = securityService.verifyTokenJson(token);
                UserDetails userDetails = prepareUserDetails(jsonUserDetails);

                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                
        	}catch(Exception e){
        		logger.error(e.getMessage());
        	}
        	
        }

        chain.doFilter(request, response);
    }
    
    private UserDetails prepareUserDetails(String jsonUserDetails) throws JsonProcessingException, IOException{
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	JsonNode root = objectMapper.readTree(jsonUserDetails);
    	
    	String userId = root.get("_id").asText();
    	String username = root.get("username").asText();

    	
    	List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    	authorities.add(new SimpleGrantedAuthority("ROLE_PRIVATE_USER"));
    	if(root.get("roles") != null) {
        	for(JsonNode role : root.get("roles")) {
        		authorities.add(new SimpleGrantedAuthority("ROLE_" + role.asText()));    		
        	}
    	}
    	
    	return new AuthUser(userId, username, authorities);
    }
}

