import React, { Component } from 'react'
import Overskrift from '~/components/overskrift/Overskrift'
import GruppeDetaljer from './GruppeDetaljer/GruppeDetaljer'
import BestillingStatus from './BestillingStatus/BestillingStatus'
import Loading from '~/components/loading/Loading'
import Table from '~/components/table/Table'
import PersonDetaljer from './PersonDetaljer/PersonDetaljer'
import FormatIdentNr from '~/utils/FormatIdentNr'
import ContentContainer from '~/components/contentContainer/ContentContainer'
import AddButton from '~/components/button/AddButton'

import './Gruppe.less'

export default class Gruppe extends Component {
	componentDidMount() {
		this.props.getGruppe()
	}

	startBestilling = () => {
		const { gruppeId } = this.props.match.params
		this.props.history.push(`/gruppe/${gruppeId}/bestilling`)
	}

	render() {
		const { gruppe, fetching, testbrukere, testbrukerFetching, getGruppe } = this.props

		if (fetching) return <Loading label="laster gruppe" panel />

		if (!gruppe) return null

		return (
			<div id="gruppe-container">
				<Overskrift
					label={gruppe.navn}
					actions={[
						{
							icon: 'edit',
							onClick: () => {
								alert('ikke implementert')
							}
						},
						{
							icon: 'trashcan',
							onClick: () => {
								alert('ikke implementert')
							}
						}
					]}
				/>
				<GruppeDetaljer gruppe={gruppe} />

				{gruppe.bestillinger.map(bestilling => (
					<BestillingStatus key={bestilling.id} bestilling={bestilling} onGroupUpdate={getGruppe} />
				))}

				<Overskrift type="h2" label="Testpersoner" />
				{gruppe.testidenter.length <= 0 ? (
					<ContentContainer>Det finnes ingen data i denne gruppen enda</ContentContainer>
				) : (
					<Table>
						<Table.Header>
							<Table.Column width="15" value="ID" />
							<Table.Column width="15" value="ID-type" />
							<Table.Column width="30" value="Navn" />
							<Table.Column width="20" value="Kjønn" />
							<Table.Column width="10" value="Alder" />
						</Table.Header>

						{testbrukerFetching ? (
							<Loading label="laster testbrukere" panel />
						) : (
							testbrukere &&
							testbrukere.map((bruker, idx) => {
								return (
									<Table.Row
										key={idx}
										expandComponent={<PersonDetaljer brukerData={bruker.data} />}
									>
										<Table.Column width="15" value={FormatIdentNr(bruker.id)} />
										<Table.Column width="15" value={bruker.idType} />
										<Table.Column width="30" value={bruker.navn} />
										<Table.Column width="20" value={bruker.kjonn} />
										<Table.Column width="10" value={bruker.alder} />
									</Table.Row>
								)
							})
						)}
					</Table>
				)}
				<AddButton title="Opprett ny bestilling" onClick={this.startBestilling} />
			</div>
		)
	}
}
