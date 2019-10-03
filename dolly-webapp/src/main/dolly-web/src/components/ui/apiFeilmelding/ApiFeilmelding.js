import React from 'react'
import cn from 'classnames'
import './ApiFeilmelding.less'

export default function ApiFeilmelding({ feilmelding, container }) {
	const css = cn('api-feilmelding', {
		'api-feilmelding-container': container
	})
	return <pre className={css}>{feilmelding}</pre>
}
