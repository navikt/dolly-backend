import React from 'react'
import { NavLink } from 'react-router-dom'
import Icon from '~/components/icon/Icon'
import logo from '~/assets/img/nav-logo-hvit.png'

import './Header.less'

export default () => {
	return (
		<div id="dolly-header">
			<NavLink to="/" className="home-nav">
				<img alt="NAV logo" src={logo} />
				<h1>Dolly</h1>
			</NavLink>

			<div className="menu-links">
				<NavLink to="/">Testdata</NavLink>
				<NavLink to="/">Kømanager</NavLink>
				<NavLink to="/">API-dok</NavLink>
			</div>

			<div id="header-user-name">
				<NavLink to="/profil">
					<Icon kind="user" size="20" /> L148286
				</NavLink>
			</div>
		</div>
	)
}
