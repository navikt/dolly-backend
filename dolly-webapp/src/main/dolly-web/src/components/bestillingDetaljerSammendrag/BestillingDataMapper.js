import Formatters from '~/utils/DataFormatter'

const _getTpsfBestillingData = data => {
	return [
		{
			label: 'Identtype',
			value: data.identtype
		},
		{
			label: 'Født etter',
			value: Formatters.formatDate(data.foedtEtter)
		},
		{
			label: 'Født før',
			value: Formatters.formatDate(data.foedtFoer)
		},
		{
			label: 'Dødsdato',
			value: Formatters.formatDate(data.doedsdato)
		},
		{
			label: 'Statsborgerskap',
			value: data.statsborgerskap,
			apiKodeverkId: 'StatsborgerskapFreg'
		},
		{
			label: 'Kjønn',
			value: Formatters.kjonnToString(data.kjonn)
		},
		{
			label: 'Sivilstand',
			value: data.sivilstand,
			apiKodeverkId: 'Sivilstander'
		},
		{
			label: 'Diskresjonskoder',
			value: data.spesreg,
			apiKodeverkId: 'Diskresjonskoder'
		},
		{
			label: 'Språk',
			value: data.sprakKode,
			apiKodeverkId: 'Språk'
		},
		{
			label: 'Egenansatt',
			value: Formatters.oversettBoolean(data.egenansattDatoFom)
		}
	]
}

export function mapBestillingData(bestillingData) {
	if (!bestillingData) return null
	const data = []

	const bestillingsInfo = {
		header: 'Bestillingsinformasjon',
		items: [
			{
				label: 'Antall',
				value: bestillingData.antallIdenter.toString()
			},
			{
				label: 'Sist Oppdatert',
				value: Formatters.formatDate(bestillingData.sistOppdatert)
			},
			{
				label: 'Gjenopprett fra',
				value: bestillingData.opprettetFraId && 'Bestilling #' + bestillingData.opprettetFraId
			}
		]
	}
	data.push(bestillingsInfo)

	// Gamle bestillinger har ikke tpsfKriterie
	if (bestillingData.tpsfKriterier) {
		const tpsfKriterier = JSON.parse(bestillingData.tpsfKriterier)
		const personinfo = {
			header: 'Personlig informasjon',
			items: _getTpsfBestillingData(tpsfKriterier)
		}
		data.push(personinfo)

		if (tpsfKriterier.boadresse) {
			const adresse = {
				header: 'Bostedadresse',
				items: [
					{
						header: 'Bosted'
					},
					{
						label: 'Gatenavn',
						value: tpsfKriterier.boadresse.gateadresse
					},
					{
						label: 'Husnummer',
						value: tpsfKriterier.boadresse.husnummer
					},
					{
						label: 'Postnummer',
						value: tpsfKriterier.boadresse.postnr
					},
					{
						label: 'Kommunenummer',
						value: tpsfKriterier.boadresse.kommunenr
					},
					{
						label: 'Flyttedato',
						value: Formatters.formatDate(tpsfKriterier.boadresse.flyttedato)
					}
				]
			}
			data.push(adresse)
		}

		if (tpsfKriterier.relasjoner) {
			if (tpsfKriterier.relasjoner.partner) {
				const partner = {
					header: 'Partner',
					items: _getTpsfBestillingData(tpsfKriterier.relasjoner.partner)
				}

				data.push(partner)
			}

			if (tpsfKriterier.relasjoner.barn) {
				const barn = {
					header: 'Barn',
					itemRows: []
				}

				tpsfKriterier.relasjoner.barn.forEach((item, i) => {
					barn.itemRows.push([
						{
							label: '',
							value: `#${i + 1}`,
							width: 'x-small'
						},
						..._getTpsfBestillingData(item)
					])
				})

				data.push(barn)
			}
		}
	}

	if (bestillingData.bestKriterier) {
		const registreKriterier = JSON.parse(bestillingData.bestKriterier)

		const aaregKriterier = registreKriterier.aareg && registreKriterier.aareg
		if (aaregKriterier) {
			const aareg = {
				header: 'Arbeidsforhold',
				itemRows: []
			}

			aaregKriterier.forEach((arbeidsforhold, i) => {
				aareg.itemRows.push([
					{
						label: '',
						value: `#${i + 1}`,
						width: 'x-small'
					},

					{
						label: 'Startdato',
						value: arbeidsforhold.ansettelsesPeriode.fom.split('T')[0]
					},
					{
						label: 'Sluttdato',
						value: arbeidsforhold.ansettelsesPeriode.tom.split('T')[0]
					},
					{
						label: 'Stillingprosent',
						value: arbeidsforhold.arbeidsavtale.stillingsprosent
					},
					{
						label: 'Type av arbeidsgiver',
						value: arbeidsforhold.arbeidsgiver.aktoertype
					},
					{
						label: 'Orgnummer',
						value: arbeidsforhold.arbeidsgiver.orgnummer
					},
					{
						label: 'Arbeidsgiver Ident',
						value: arbeidsforhold.arbeidsgiver.ident
					},
					{
						label: 'Yrke',
						value: arbeidsforhold.arbeidsavtale.yrke,
						apiKodeverkId: 'Yrker',
						width: 'xlarge',
						showKodeverkValue: true
					}
				])
			})

			data.push(aareg)
		}
		const sigrunStubKriterier = registreKriterier.sigrunStub && registreKriterier.sigrunStub

		if (sigrunStubKriterier) {
			// Flatter ut sigrunKriterier for å gjøre det lettere å mappe

			let flatSigrunStubKriterier = []
			sigrunStubKriterier.forEach(inntekt => {
				inntekt.grunnlag.forEach(g => {
					flatSigrunStubKriterier.push({
						grunnlag: g.tekniskNavn,
						inntektsaar: inntekt.inntektsaar,
						tjeneste: inntekt.tjeneste,
						verdi: g.verdi
					})
				})
			})

			const sigrunStub = {
				header: 'Inntekter',
				itemRows: []
			}

			flatSigrunStubKriterier.forEach((inntekt, i) => {
				sigrunStub.itemRows.push([
					{
						label: '',
						value: `#${i + 1}`,
						width: 'x-small'
					},
					{
						label: 'År',
						value: inntekt.inntektsaar
					},
					{ label: 'Beløp', value: inntekt.verdi },
					{
						label: 'Tjeneste',
						value: inntekt.tjeneste
					},
					{
						label: 'grunnlag',
						value: inntekt.grunnlag,
						width: 'xlarge',
						apiKodeverkId: inntekt.tjeneste
					}
				])
			})
			data.push(sigrunStub)
		}

		const krrKriterier = registreKriterier.krrStub && registreKriterier.krrStub

		if (krrKriterier) {
			const krrStub = {
				header: 'Kontaktinformasjon og reservasjon',
				items: [
					{
						label: 'Mobilnummer',
						value: krrKriterier.mobil
					},
					{
						label: 'Epost',
						value: krrKriterier.epost
					},
					{
						label: 'RESERVERT MOT DIGITALKOMMUNIKASJON',
						value: krrKriterier.reservert ? 'JA' : 'NEI',
						width: 'medium'
					}
				]
			}

			data.push(krrStub)
		}
	}

	return data
}
