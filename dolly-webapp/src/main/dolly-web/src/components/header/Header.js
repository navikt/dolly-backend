import React from 'react'
import { NavLink } from 'react-router-dom'
import Icon from '~/components/icon/Icon'
import logo from '~/assets/img/nav-logo-hvit.png'

import './Header.less'

export default ({ brukerData }) => {
	return (
		<header className="app-header">
			<NavLink to="/" className="home-nav">
				<div className="img-logo">
					<img alt="NAV logo" src={logo} />
				</div>
				<h1>Dolly</h1>
			</NavLink>

			<div className="menu-links">
				<NavLink to="/">Testdatagrupper</NavLink>
				<NavLink to="/team">Team</NavLink>
				<NavLink to="/tpsendring">TPS-Endringsmelding</NavLink>
				{/* <NavLink to="/kømanager">Kømanager</NavLink> */}
				<a href="/swagger-ui.html" target="_blank">
					API-dok
				</a>
			</div>

			<NavLink to="/profil" className="header-user-name">
				<Icon kind="user" size="20" /> {brukerData.navIdent}
			</NavLink>
		</header>
	)
}
