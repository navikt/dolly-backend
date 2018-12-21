import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import Tooltip from 'rc-tooltip'
import HjelpeTekst from 'nav-frontend-hjelpetekst'
import Icon from '~/components/icon/Icon'
import Button from '~/components/button/Button'

import './ContentTooltip.less'

export default class ContentTooltip extends PureComponent {
	static propTypes = {
		children: PropTypes.node.isRequired
	}

	state = {
		isOpen: false
	}

	_handleOnClick = () => {
		console.log('hei')
		this.setState({ isOpen: !this.state.isOpen })
		console.log('stae', this.state.isOpen)
	}
	render() {
		return (
			// <HjelpeTekst className="content-tooltip" id="hjelpetekst" type="under-hoyre">
			// 	{this.props.children}
			// </HjelpeTekst>
			<div className="flexbox--align-center" onClick={this._handleOnClick}>
				<div className="hjelpetekst">
					{this._renderHjelpeIkon()}
					{this.state.isOpen && this._renderHjelpeTekst()}
				</div>
				<p className="clickable-text-small">Hjelp</p>
			</div>
		)
	}

	_renderHjelpeIkon = () => (
		<button
			type="button"
			className="hjelpetekst__apneknapp"
			title="Hjelptekst"
			aria-label="Hjelptekst"
			aria-pressed={this.state.isOpen ? 'true' : 'false'}
			aria-describedby={this.state.isOpen ? 'tooltip-hjelpetekst' : null}
		>
			<span className="sr-only">Hjelptekst</span>
			<svg
				kind="help-circle"
				className="hjelpetekst__anchor"
				focusable="false"
				height="32"
				width="32"
				viewBox="0 0 18.22 18.22"
			>
				<title>Hjelp</title>
				{this.state.isOpen ? (
					<path
						fill="#2868B3"
						d="M9.1 0C4.1 0 0 4.1 0 9.1s4.1 9.1 9.1 9.1 9.1-4.1 9.1-9.1S14.1 0 9.1 0zm0 14.7c-.5 
				0-.9-.4-.9-.9s.4-.9.9-.9.9.4.9.9-.4.9-.9.9zM9.6 9v1.9c0 .3-.2.5-.5.5s-.5-.2-.5-.5V8.5c0-.3.2-.5.5-.5 1 0
				 1.9-.8 1.9-1.8s-.8-1.9-1.9-1.9-1.8.8-1.8 1.8c0 .3-.2.5-.5.5s-.5-.2-.5-.5c0-1.6 1.3-2.9 2.8-2.9 1.4 0 2.5 
				 1 2.8 2.4.3 1.6-.7 3.1-2.3 3.4z"
					/>
				) : (
					<Fragment>
						<path
							fill="none"
							d="M9.11 1a8.11 8.11 0 1 0 8.11 8.11A8.12 8.12 0 0 0 9.11 1zm0 13.7a.89.89 0 1 1 .89-.9.89.89 0 0 1-.89.9zm.5-5.7v1.89a.5.5 0 0 1-1 0V8.5a.5.5 0 0 1 .5-.5 1.85 1.85 0 1 0-1.85-1.85.5.5 0 0 1-1 0A2.85 2.85 0 1 1 9.61 9z"
						/>
						<path
							fill="#2968b2"
							d="M9.11 0a9.11 9.11 0 1 0 9.11 9.11A9.12 9.12 0 0 0 9.11 0zm0 17.22a8.11 8.11 0 1 1 8.11-8.11 8.12 8.12 0 0 1-8.11 8.11z"
						/>
						<path
							fill="#2968b2"
							d="M9.11 3.3a2.85 2.85 0 0 0-2.85 2.85.5.5 0 0 0 1 0A1.85 1.85 0 1 1 9.11 8a.5.5 0 0 0-.5.5v2.35a.5.5 0 0 0 1 0V9a2.85 2.85 0 0 0-.5-5.65z"
						/>
						<circle fill="#2968b2" cx="9.11" cy="13.8" r=".89" />
					</Fragment>
				)}
			</svg>
		</button>
	)

	_renderHjelpeTekst = () => (
		<div
			tabIndex="-1"
			id="tooltip-hjelpetekst"
			role="tooltip"
			className="hjelpetekst__tooltip content-tooltip hjelpetekst__tooltip--under"
		>
			<div className="hjelpetekst__tekst">
				<p className="typo-normal">Testdatagruppen inneholder alle testpersonene dine (FNR/DNR).</p>
			</div>
			<button
				className="lukknapp lukknapp--hvit"
				aria-controls="tooltip-hjelpetekst"
				aria-label="Lukk hjelpetekst"
				onClick={this._handleOnClick}
			>
				Lukk hjelpetekst
			</button>
		</div>
	)
}
