<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!-- saved from url=(0091)https://www.zm.secure.barclays.com/bir/feature/loginprocess?execution=e1s1&_t=1407945783138 -->
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Log in - Barclays Internet Banking
</title>
<link href="file:///C|/jcodes/dev/appservers/etax/webapps/resources/css/barclay/reset.css" type="text/css" rel="stylesheet">
<link href="file:///C|/jcodes/dev/appservers/etax/webapps/resources/css/barclay/common.css" type="text/css" rel="stylesheet">
<link href="file:///C|/jcodes/dev/appservers/etax/webapps/resources/css/barclay/login.css" type="text/css" rel="stylesheet">
<link href="file:///C|/jcodes/dev/appservers/etax/webapps/resources/css/barclay/cpsKeyboard.css" type="text/css" rel="stylesheet">
<link href="file:///C|/jcodes/dev/appservers/etax/webapps/resources/css/barclay/thickbox.css" rel="stylesheet" type="text/css">
<link href="file:///C|/jcodes/dev/appservers/etax/webapps/resources/css/barclay/tips.css" rel="stylesheet" type="text/css">
<link href="file:///C|/jcodes/dev/appservers/etax/webapps/resources/css/barclay/keyboardstyle.css" type="text/css" rel="stylesheet">
</head>
<body style="background: #EFEBEF; font-family:Tahoma;color: #333; font-size:12px;">
<div id="blindNav" style="display: none">
<p>Navigation for visually impaired users:<a href="https://www.zm.secure.barclays.com/bir/feature/loginprocess?execution=e1s1&_t=1407945783138#top_menu_anchor" id="alt_N" accesskey="n">skip to
navigation</a>,<a href="https://www.zm.secure.barclays.com/bir/feature/loginprocess?execution=e1s1&_t=1407945783138#sidebar_menu_anchor" id="alt_R" accesskey="r">skip
to right menu</a>, or <a href="https://www.zm.secure.barclays.com/bir/feature/loginprocess?execution=e1s1&_t=1407945783138#content_anchor" id="alt_S" accesskey="s">skip
to main content</a>.</p>
</div>
<div id="bg">
<div class="frame">
<div class="left alpha_0" style=""></div>
<div class="middle alpha_0">
<div tabindex="-1&#39;" title="Welcome to Barclays Internet Banking" role="banner" class="logo">
<img src="file:///C|/jcodes/dev/appservers/etax/webapps/resources/css/barclay/barclays.gif">
</div>
<div class="pnl">
<div role="form" class="loginPnl"><form id="loginForm" name="loginForm" method="post" autocomplete="off" action="file:///C|/jcodes/dev/appservers/etax/webapps/resources/css/barclay/Log in - Barclays Internet Banking.htm" enctype="application/x-www-form-urlencoded" onkeyup="onFormEnterEvent(this,&#39;loginForm:loginLink&#39;, event);"><input type="hidden" value="1982114790" name="CSRF_TOKEN">
<div class="LanguageSelect">
</div>
<div>
<div class="font3">Welcome to</div>
<div class="font3 bold">Barclays Internet Banking</div>
<br><br><table class="ssc_form_table" border="0" cellpadding="0" cellspacing="0" id="loginForm:j_id21"><tbody><tr><td class="ssc_form_label"><label for="loginForm:loginName">Username</label></td><td class="ssc_form_value"><span class="bgSpan"><input id="loginForm:loginName" name="loginForm:loginName" type="text" maxlength="1000" autocomplete="off"></span></td></tr><tr><td class="ssc_form_label">
<br></td><td class="ssc_form_value"></td></tr><tr><td class="ssc_form_label"><label for="loginForm:password">Password</label></td><td class="ssc_form_value"><span class="bgSpan"><input type="password" id="loginForm:password" name="loginForm:password" maxlength="1000" autocomplete="off"></span></td></tr><tr><td class="ssc_form_label">
<br></td><td class="ssc_form_value"></td></tr></tbody></table>
<div class="indent">
<div>Forgot Your<a class="ftBlue" href="https://www.zm.secure.barclays.com/bir/feature/forgotUsername">Username</a> / <a class="ftBlue" href="https://www.zm.secure.barclays.com/bir/feature/forgotPassword">Password?</a></div>
<div id="dynamicKeyboard" align="left"><table id="loginForm:j_id33" cellspacing="0" cellpadding="0" border="0"><tbody><tr id="loginForm:j_id34"><td id="loginForm:j_id35">
<span role="checkbox" class="js_checkbox" aria-checked="false"></span><input id="loginForm:dkbCheckbox" type="checkbox" name="loginForm:dkbCheckbox" value="true" onclick="dkbCheckbox(this,&#39;loginForm:password&#39;);" class="js_helper_hidden_accessible"><label for="loginForm:dkbCheckbox">Turn on Dynamic Keyboard</label></td></tr></tbody></table>
</div>
</div>
<p class="indent">By logging in, you have agreed to the <a class="ftBlue" target="_blank" href="http://www.barclays.com/africa/zambia/downloads.html">Terms &amp; Conditions</a> of Internet Banking.
</p>
</div>
<br>
<div class="button">
<div>
<div class="secButton"><img src="file:///C|/jcodes/dev/appservers/etax/webapps/resources/css/barclay/logo_secure.gif"></div>
<div class="loginButton btn fr"><script type="text/javascript">


	function sscSetHiddenInput(formname, name, value)
	{
		var form = document.forms[formname];
		if(typeof form.elements[name]=='undefined')
		{
			var newInput = document.createElement('input');
			newInput.setAttribute('type','hidden');
			newInput.setAttribute('id',name);
			newInput.setAttribute('name',name);
			newInput.setAttribute('value',value);
			form.appendChild(newInput);
		}
		else
		{
			form.elements[name].value=value;
		}
		
	}
	
	
	function sscClearHiddenInput(formname, name, value)
	{
		var form = document.forms[formname];
		if(typeof form.elements[name]!='undefined')
		{
			form.elements[name].value=null;
		}
		
	}
	
	function sscSubmitForm(formName, linkId, target, params)
	{
		var cursorWait = true;for(var i=0; params!=null && i < params.length; i++){if(params[i][0]=='cursorWait' && params[i][1]=='false'){cursorWait=false;break;}}if(cursorWait){var tags = 'BODY,A,INPUT,SELECT,RADIO,TEXTAREA,TABLE,SPAN,DIV'.split(',');for(var i=0;i < tags.length;i++){var eles = document.getElementsByTagName(tags[i]);for(var j=0;j < eles.length;j++){eles[j].style.cursor='wait';}}}
		var clearFn = 'clearFormHiddenParams_'+formName.replace(/-/g, '\$:').replace(/:/g,'_');
		if(typeof eval('window.'+clearFn)=='function')
		{
			eval('window.'+clearFn+'(formName)');
		}
		
		var oldTarget = '';
		if((typeof target=='function') && target != null)
		{
			oldTarget=document.forms[formName].target;
			document.forms[formName].target=target;
		}
		if((typeof params!='undefined') && params != null)
		{
			for(var i=0; i < params.length; i++)
			{
				sscSetHiddenInput(formName,params[i][0], params[i][1]);
			}
			
		}
		
		sscSetHiddenInput(formName,formName +':'+'_idcl',linkId);
		
		if(document.forms[formName].onsubmit)
		{
			var result=document.forms[formName].onsubmit();
			if((typeof result=='undefined')||result)
			{
				document.forms[formName].submit();
			}
			
		}
		else 
		{
			document.forms[formName].submit();
		}
		if(oldTarget==null) oldTarget='';
		document.forms[formName].target=oldTarget;
		if((typeof params!='undefined') && params != null)
		{
			for(var i=0; i < params.length; i++)
			{
				sscClearHiddenInput(formName,params[i][0], params[i][1]);
			}
			
		}
		
		sscClearHiddenInput(formName,formName +':'+'_idcl',linkId);return false;
	}
	

</script><a href="https://www.zm.secure.barclays.com/bir/feature/loginprocess?execution=e1s1&_t=1407945783138#" onclick=" return sscSubmitForm(&#39;loginForm&#39;,&#39;loginForm:loginLink&#39;,null,[[&#39;forward&#39;,&#39;true&#39;]]);" id="loginForm:loginLink" class="directional_0 lColor4 short">Log in
<em class="tl"></em>
<em class="tr"></em>
<em class="bl"></em>
<em class="br"></em></a>
</div>
</div>
<div id="bubble" class="dialogue" style="display: none;">Your username must be at least 8-16 characters.
<br><hr>Your password must be at least 8 characters.
</div>
<div class="helpText">
<div class="tips"><a id="loginForm:j_id47" class="progressiveLink" onclick="Spring.remoting.submitForm(&#39;loginForm:j_id47&#39;, &#39;loginForm&#39;, {processIds: &#39;loginForm:j_id47, *&#39;, width : &#39;760&#39;, height : &#39;480&#39;, TB_window : &#39;true&#39;}); return false;" href="https://www.zm.secure.barclays.com/bir/feature/loginprocess?execution=e1s1&_t=1407945783138#" name="loginForm:j_id47">Security Tips</a>
</div>
<div class="help"><a href="javascript:void(0)" id="help">Help</a></div>
</div>
</div>
<input type="hidden" name="_t" value="1407945784185"><input type="hidden" name="loginForm_SUBMIT" value="1"><input type="hidden" name="javax.faces.ViewState" id="javax.faces.ViewState" value="e1s1"></form>
</div>
<div class="midPnl">
<div class="font3">New to</div>
<div class="font3 bold">Barclays Internet Banking?</div>
<div style="line-height: 1.2;"></div>
<br>
<p><span>Register now for Online Banking and enjoy the convenience.</span></p>
<div><form id="loginFormHelp" name="loginFormHelp" method="post" autocomplete="off" action="file:///C|/jcodes/dev/appservers/etax/webapps/resources/css/barclay/Log in - Barclays Internet Banking.htm" enctype="application/x-www-form-urlencoded"><input type="hidden" value="1982114790" name="CSRF_TOKEN"><a id="loginFormHelp:j_id61" class="helpLink progressiveLink" onclick="Spring.remoting.submitForm(&#39;loginFormHelp:j_id61&#39;, &#39;loginFormHelp&#39;, {processIds: &#39;loginFormHelp:j_id61, *&#39;, width : &#39;760&#39;, height : &#39;480&#39;, TB_window : &#39;true&#39;}); return false;" href="https://www.zm.secure.barclays.com/bir/feature/loginprocess?execution=e1s1&_t=1407945783138#" name="loginFormHelp:j_id61"></a><input type="hidden" name="loginFormHelp_SUBMIT" value="1"><input type="hidden" name="javax.faces.ViewState" id="javax.faces.ViewState" value="e1s1"></form>
</div>
<div class="registButton">
<div class="btn fl">
<a title="Sign-Up" aria-pressed="false" role="button" class="functional lColor4" href="https://www.zm.secure.barclays.com/bir/feature/onlineRegistration"><span>Register Now</span>
<span class="arrow icon"></span>
<em class="tl"></em>
<em class="tr"></em>
<em class="bl"></em>
<em class="br"></em>
</a>
</div>
</div>
<div class="dashborder">
<div class="contact_us">
<p>Contact us: +260 211 366230 - 1 or 5950</p>
<p>Mon-Fri During normal working hours.</p>
<p>Email: customerservice.zambia@barclays.com</p>
</div>
</div>
</div>
<div class="regPnl">
<div class="login_right"><h3></h3>
<ul>
<li>» <a href="https://www.zm.secure.barclays.com/bir/feature/forgotUsername">Forgot username</a></li>
<li>» <a href="https://www.zm.secure.barclays.com/bir/feature/forgotPassword">Forgot password</a></li>
<li>» <a href="https://www.zm.secure.barclays.com/bir/feature/forgotSQA">Forgot secret question</a></li>
<li>» <a href="http://www.barclays.com/africa/zambia/cash-machine-locations.html">Nearest branch/ATM</a></li>
<li>» <a href="http://www.barclays.com/africa/zambia/downloads.html">Online Banking FAQs</a></li>
<li>» <a href="https://www.zm.secure.barclays.com/bir/feature/onlineRegistration?PREREGISTER=true">Pre-registered customer fulfillment</a></li>
</ul>
</div>
<div class="rhtbanner">
<div>
<a href="http://www.barclays.com/africa/zambia/sms-alerts.html" title="Get your account updated by SMS" target="_blank">
<img src="file:///C|/jcodes/dev/appservers/etax/webapps/resources/css/barclay/1290534220794.jpg" alt="Barclays Premier">
</a>
</div>
</div>
</div>
</div>
</div>
<div class="right alpha_0"></div>
</div>
<div class="dkb">
<div id="keyboard" class="keyboard ui-draggable">
<div id="close" class="close"><img height="17" width="24" alt="keyboard" src="file:///C|/jcodes/dev/appservers/etax/webapps/resources/css/barclay/keyboard_16.gif"></div>
<div id="keyboard-bg" class="kbBackground">
<div id="row0">
<input name="^" type="button" class="short" value="^"><!--
                   	--><input name="!" type="button" class="short" value="!"><!--
                    --><input name="&quot;" type="button" class="short" value="&quot;"><!--
                    --><input name="§" type="button" class="short" value="§"><!--
                    --><input name="$" type="button" class="short" value="$"><!--
                    --><input name="%" type="button" class="short" value="%"><!--
                    --><input name="&amp;" type="button" class="short" value="&amp;"><!--
                    --><input name="/" type="button" class="short" value="/"><!--
                    --><input name="(" type="button" class="short" value="("><!--
                    --><input name=")" type="button" class="short" value=")"><!--
                    --><input name="=" type="button" class="short" value="="><!--
                    --><input name="?" type="button" class="short" value="?"><!--
                    --><input name="\" type="button" class="short" value="\">
</div>
<div id="shift_row0" class="row0Shift">
<input name="^" type="button" class="short" value="^"><!--
                    --><input name="!" type="button" class="short" value="!"><!--
                    --><input name="&quot;" type="button" class="short" value="&quot;"><!--
                    --><input name="§" type="button" class="short" value="§"><!--
                    --><input name="$" type="button" class="short" value="$"><!--
                    --><input name="%" type="button" class="short" value="%"><!--
                    --><input name="&amp;" type="button" class="short" value="&amp;"><!--
                    --><input name="/" type="button" class="short" value="/"><!--
                    --><input name="(" type="button" class="short" value="("><!--
                    --><input name=")" type="button" class="short" value=")"><!--
                    --><input name="=" type="button" class="short" value="="><!--
                    --><input name="?" type="button" class="short" value="?"><!--
                    --><input name="\" type="button" class="short" value="\">
</div>
<div id="row1">
<input name="1" type="button" class="short" value="1"><!--
                    --><input name="2" type="button" class="short" value="2"><!--
                    --><input name="3" type="button" class="short" value="3"><!--
                    --><input name="4" type="button" class="short" value="4"><!--
                    --><input name="5" type="button" class="short" value="5"><!--
                    --><input name="6" type="button" class="short" value="6"><!--
                    --><input name="7" type="button" class="short" value="7"><!--
                    --><input name="8" type="button" class="short" value="8"><!--
                    --><input name="9" type="button" class="short" value="9"><!--
                    --><input name="0" type="button" class="short" value="0"><!--
                    --><input name="ß" type="button" class="short" value="ß"><!--
                    --><input name="*" type="button" class="short" value="*"><!--
                    --><input name="~" type="button" class="short" value="~">
</div>
<div id="shift_row1" class="row1Shift">
<input name="1" type="button" class="short" value="1"><!--
                    --><input name="2" type="button" class="short" value="2"><!--
                    --><input name="3" type="button" class="short" value="3"><!--
                    --><input name="4" type="button" class="short" value="4"><!--
                    --><input name="5" type="button" class="short" value="5"><!--
                    --><input name="6" type="button" class="short" value="6"><!--
                    --><input name="7" type="button" class="short" value="7"><!--
                    --><input name="8" type="button" class="short" value="8"><!--
                    --><input name="9" type="button" class="short" value="9"><!--
                    --><input name="0" type="button" class="short" value="0"><!--
                    --><input name="ß" type="button" class="short" value="ß"><!--
                    --><input name="*" type="button" class="short" value="*"><!--
                    --><input name="~" type="button" class="short" value="~">
</div>
<div id="row2">
<input name="q" type="button" class="short" value="q"><!--
                    --><input name="w" type="button" class="short" value="w"><!--
                    --><input name="e" type="button" class="short" value="e"><!--
                    --><input name="r" type="button" class="short" value="r"><!--
                    --><input name="t" type="button" class="short" value="t"><!--
                    --><input name="y" type="button" class="short" value="y"><!--
                    --><input name="u" type="button" class="short" value="u"><!--
                    --><input name="i" type="button" class="short" value="i"><!--
                    --><input name="o" type="button" class="short" value="o"><!--
                    --><input name="p" type="button" class="short" value="p"><!--
                    --><input name="@" type="button" class="short" value="@"><!--
                    --><input name="+" type="button" class="short" value="+"><!--
                    --><input name="&#39;" type="button" class="short" value="&#39;">
</div>
<div id="shift_row2" class="row2Shift">
<input name="Q" type="button" class="short" value="Q"><!--
                    --><input name="W" type="button" class="short" value="W"><!--
                    --><input name="E" type="button" class="short" value="E"><!--
                    --><input name="R" type="button" class="short" value="R"><!--
                    --><input name="T" type="button" class="short" value="T"><!--
                    --><input name="Y" type="button" class="short" value="Y"><!--
                    --><input name="U" type="button" class="short" value="U"><!--
                    --><input name="I" type="button" class="short" value="I"><!--
                    --><input name="O" type="button" class="short" value="O"><!--
                    --><input name="P" type="button" class="short" value="P"><!--
                    --><input name="@" type="button" class="short" value="@"><!--
                    --><input name="+" type="button" class="short" value="+"><!--
                    --><input name="&#39;" type="button" class="short" value="&#39;">
</div>
<div id="row3">
<input name="a" type="button" class="short" value="a"><!--
                    --><input name="s" type="button" class="short" value="s"><!--
                    --><input name="d" type="button" class="short" value="d"><!--
                    --><input name="f" type="button" class="short" value="f"><!--
                    --><input name="g" type="button" class="short" value="g"><!--
                    --><input name="h" type="button" class="short" value="h"><!--
                    --><input name="j" type="button" class="short" value="j"><!--
                    --><input name="k" type="button" class="short" value="k"><!--
                    --><input name="l" type="button" class="short" value="l"><!--
                    --><input name="#" type="button" class="short" value="#"><!--
                    --><input name="," type="button" class="short" value=","><!--
                    --><input name="." type="button" class="short" value="."><!--
                    --><input name="-" type="button" class="short" value="-">
</div>
<div id="shift_row3" class="row3Shift">
<input name="A" type="button" class="short" value="A"><!--
                    --><input name="S" type="button" class="short" value="S"><!--
                    --><input name="D" type="button" class="short" value="D"><!--
                    --><input name="F" type="button" class="short" value="F"><!--
                    --><input name="G" type="button" class="short" value="G"><!--
                    --><input name="H" type="button" class="short" value="H"><!--
                    --><input name="J" type="button" class="short" value="J"><!--
                    --><input name="K" type="button" class="short" value="K"><!--
                    --><input name="L" type="button" class="short" value="L"><!--
                    --><input name="#" type="button" class="short" value="#"><!--
                    --><input name="," type="button" class="short" value=","><!--
                    --><input name="." type="button" class="short" value="."><!--
                    --><input name="-" type="button" class="short" value="-">
</div>
<div id="row4">
<input name="z" type="button" class="short" value="z"><!--
                    --><input name="x" type="button" class="short" value="x"><!--
                    --><input name="c" type="button" class="short" value="c"><!--
                    --><input name="v" type="button" class="short" value="v"><!--
                    --><input name="b" type="button" class="short" value="b"><!--
                    --><input name="n" type="button" class="short" value="n"><!--
                    --><input name="m" type="button" class="short" value="m"><!--
                    --><input name=";" type="button" class="short" value=";"><!--
                    --><input name=":" type="button" class="short" value=":"><!--
                    --><input name="_" type="button" class="short" value="_"><!--
                    --><input name="&lt;" type="button" class="short" value="&lt;"><!--
                    --><input name="&gt;" type="button" class="short" value="&gt;"><!--
                    --><input name="{" type="button" class="short" value="{">
</div>
<div id="shift_row4" class="row4Shift">
<input name="Z" type="button" class="short" value="Z"><!--
                    --><input name="X" type="button" class="short" value="X"><!--
                    --><input name="C" type="button" class="short" value="C"><!--
                    --><input name="V" type="button" class="short" value="V"><!--
                    --><input name="B" type="button" class="short" value="B"><!--
                    --><input name="N" type="button" class="short" value="N"><!--
                    --><input name="M" type="button" class="short" value="M"><!--
                    --><input name=";" type="button" class="short" value=";"><!--
                    --><input name=":" type="button" class="short" value=":"><!--
                    --><input name="_" type="button" class="short" value="_"><!--
                    --><input name="&lt;" type="button" class="short" value="&lt;"><!--
                    --><input name="&gt;" type="button" class="short" value="&gt;"><!--
                    --><input name="{" type="button" class="short" value="{">
</div>
<div id="row5">
<input type="button" value="Clear" class="medium" name="clear"><!--
		            --><input type="button" id="CapOn" value="Cap" class="CapOn medium" name="cap"><!--
		            --><input type="button" value="Backspace" class="long" name="Backspace"><!--
                    --><input name="}" type="button" class="short" value="}"><!--
                    --><input name="[" type="button" class="short" value="["><!--
                    --><input name="]" type="button" class="short" value="]">
</div>
<div id="shift_row5" class="row5Shift">
<input type="button" value="Clear" class="medium" name="clear"><!--
		            --><input type="button" id="CapOn" value="Cap" class="CapOn medium" name="cap"><!--
		            --><input type="button" value="Backspace" class="long" name="Backspace"><!--
		            --><input name="}" type="button" class="short" value="}"><!--
                    --><input name="[" type="button" class="short" value="["><!--
                    --><input name="]" type="button" class="short" value="]">
</div>
</div>
</div>
</div>
<!-- footer -->
<div class="footer">
<div class="left alpha_0"></div>
<div class="middle alpha_0">
<label class="hiddenLabel" id="footerLinks">Footer Links</label>
<ul tabindex="-1" aria-labelledby="footerLinks" role="navigation">
<li class="first"><a href="http://www.barclays.com/africa/zambia/downloads.html" target="_blank">Schedule of Charges</a></li>
<li><a>|</a></li>
<li><a href="http://www.barclays.com/africa/zambia/downloads.html" target="_blank">Terms &amp; Conditions</a></li>
</ul>
<div tabindex="-1" role="contentinfo" id="contentinfo">
<h5>
<img src="file:///C|/jcodes/dev/appservers/etax/webapps/resources/css/barclay/premier.gif">	<br>
<span class="premierText">Proud sponsors of the Barclays Premier League</span>
</h5>
</div>
</div>
<div class="right alpha_0"></div>
</div>
<!-- End footer -->
</div>
</body>