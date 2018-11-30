import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import { Line } from 'rc-progress'
import Loading from '~/components/loading/Loading'
import './BestillingProgress.less'
import Knapp from 'nav-frontend-knapper'
import Icon from '~/components/icon/Icon'

export default class BestillingProgress extends PureComponent {
	static propTypes = {
		status: PropTypes.object.isRequired
	}

	render() {
		const { status, failed } = this.props

		return (
			<Fragment>
				<div className="flexbox--space">
					<h5>
						<Loading onlySpinner />
						{status.title}
					</h5>
					<span>{status.text}</span>
				</div>
				<div>
					<Line percent={status.percent} strokeWidth={0.5} trailWidth={0.5} strokeColor="#254b6d" />
				</div>
				{this.props.failed && (
					<div className="cancel-container">
						<div>
							<Icon kind={'report-problem-circle'} />
							<h5 className="feil-status-text">
								Dette tar lengre tid enn forventet. Noe kan ha gått galt med bestillingen din.
							</h5>
						</div>
						<Knapp type="fare">AVBRYT BESTILLING</Knapp>
					</div>
				)}
			</Fragment>
		)
	}
}
