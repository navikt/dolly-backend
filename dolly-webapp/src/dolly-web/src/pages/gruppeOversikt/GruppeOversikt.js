import React, { Component } from 'react'
import { ToggleGruppe, ToggleKnapp } from 'nav-frontend-skjema'
import Overskrift from '~/components/overskrift/Overskrift'
import Table from '~/components/table/Table'
import Input from '~/components/fields/Input/Input'
import RedigerGruppe from './RedigerGruppe/RedigerGruppe'
import './GruppeOversikt.less'

export default class GruppeOversikt extends Component {
	state = {
		visOpprettGruppe: false,
		gruppeEier: 'mine',
		editId: null
	}

	componentDidMount() {
		this.props.getGrupper()
	}

	onOpprettGruppeSuccess = () => this.setState({ visOpprettGruppe: false }, this.props.getGrupper)

	toggleVisOpprettGruppe = () => this.setState({ visOpprettGruppe: !this.state.visOpprettGruppe })

	toggleGruppeOwner = e => this.setState({ gruppeEier: e.target.value })

	render() {
		const { visOpprettGruppe } = this.state
		const { grupper, history } = this.props

		if (!grupper) return false

		return (
			<div id="gruppeoversikt-container">
				<div className="flexbox--space">
					<Overskrift
						label="Testdatagrupper"
						actions={[{ icon: 'plus-circle', onClick: this.toggleVisOpprettGruppe }]}
					/>
					<Input name="sokefelt" className="label-offscreen" label="" placeholder="Søk" />
				</div>

				<div className="flexbox--space">
					<ToggleGruppe onChange={this.toggleGruppeOwner} name="toggleGruppe">
						<ToggleKnapp value="mine" defaultChecked={true} key="1">
							Mine
						</ToggleKnapp>
						<ToggleKnapp value="alle" key="2">
							Alle
						</ToggleKnapp>
					</ToggleGruppe>
				</div>

				{visOpprettGruppe && (
					<RedigerGruppe
						onSuccess={this.onOpprettGruppeSuccess}
						onCancel={this.toggleVisOpprettGruppe}
					/>
				)}

				<Table>
					<Table.Header>
						<Table.Column width="15" value="ID" />
						<Table.Column width="20" value="Navn" />
						<Table.Column width="15" value="Team" />
						<Table.Column width="50" value="Hensikt" />
					</Table.Header>

					{grupper.map((o, idx) => {
						if (o.id === this.state.editId) {
							return (
								<RedigerGruppe
									onSuccess={this.onOpprettGruppeSuccess}
									onCancel={this.toggleVisOpprettGruppe}
								/>
							)
						}

						return (
							<Table.Row
								key={idx}
								navLink={() => history.push(`gruppe/${o.id}`)}
								editAction={() => this.setState({ editId: o.id })}
							>
								<Table.Column width="15" value={o.id.toString()} />
								<Table.Column width="20" value={o.navn} />
								<Table.Column width="15" value={o.team} />
								<Table.Column width="40" value={o.hensikt} />
							</Table.Row>
						)
					})}
				</Table>
			</div>
		)
	}
}
