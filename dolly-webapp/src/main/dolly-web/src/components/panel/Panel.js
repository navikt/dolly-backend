import React, { Component } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'
import ExpandButton from '~/components/button/ExpandButton'
import Icon from '~/components/icon/Icon'
import Checkbox from '~/components/fields/Checkbox/Checkbox'
import LinkButton from '~/components/button/LinkButton/LinkButton'
import ContentTooltip from '~/components/contentTooltip/ContentTooltip'

import './Panel.less'

export default class Panel extends Component {
	static propTypes = {
		forceOpen: PropTypes.bool,
		startOpen: PropTypes.bool,
		heading: PropTypes.node,
		content: PropTypes.node,
		errors: PropTypes.bool
	}

	static defaultProps = {
		startOpen: false,
		heading: 'Panel'
	}

	state = {
		open: this.props.startOpen
	}

	toggle = event => this.setState({ open: !this.state.open })

	render() {
		const {
			forceOpen,
			heading,
			content,
			children,
			errors,
			checkAttributeArray,
			uncheckAttributeArray
		} = this.props

		const panelIsOpen = forceOpen || this.state.open

		const panelClass = cn('panel', {
			'panel-open': panelIsOpen
		})

		const renderContent = children ? children : content

		let infotekst, infotekstHeader
		switch (heading.props.children) {
			case 'Inntekter':
				infotekstHeader = 'Lignede inntekter - årlige'	
				infotekst = 'Lignede inntekter er stemplet og godkjent. Inntektene her blir lagt i Sigrun-stub.'
				break
			// case 'Kontaktinformasjon og reservasjon':
			// 	infotekst = 'Her kommer informasjon om krr'
			// 	break
		}

		return (
			<div className={panelClass}>
				<div className="panel-heading">
					{heading}
					{infotekst && (
						<ContentTooltip>
								{infotekstHeader && <b>{infotekstHeader}: <br/></b>}
								<span>{infotekst}</span>
							<br/>
							{/* {link && <a
									style={{ color: 'lightblue' }}
									href={link}
								>
								Les mer
							</a>
							} */}
						</ContentTooltip>
					)}
					{errors && (
						<div className="panel-heading_error">
							<Icon kind="report-problem-triangle" />Feil i felter
						</div>
					)}
					<span className="panel-heading_buttons">
						{checkAttributeArray && <LinkButton text="Velg alle" onClick={checkAttributeArray} />}
						{uncheckAttributeArray && (
							<LinkButton text="Fjern alle" onClick={uncheckAttributeArray} />
						)}
						<ExpandButton expanded={panelIsOpen} onClick={this.toggle} />
					</span>
				</div>
				{panelIsOpen && <div className="panel-content">{renderContent}</div>}
			</div>
		)
	}
}
