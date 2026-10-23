<script lang="ts">
	const metrics = [
		{ value: '30,000+', label: 'pruebas de muestras realizadas', detail: 'para ensayos clínicos' },
		{ value: '50+', label: 'empresas atendidas', detail: 'en todo el mundo' },
		{ value: '10+', label: 'ensayos personalizados', detail: 'para compañías farmacéuticas y biotecnológicas' }
	];

	const capabilities = [
		{
			number: '01',
			title: 'Medicina de precisión y genómica',
			description:
				'Análisis especializados que contribuyen a generar evidencia para investigación y desarrollo en salud.',
			icon: 'genomics'
		},
		{
			number: '02',
			title: 'Bioanálisis clínico',
			description:
				'Soporte analítico para estudios clínicos desde Fase I hasta Fase III, con procesos enfocados en calidad y trazabilidad.',
			icon: 'analysis'
		},
		{
			number: '03',
			title: 'Logística internacional',
			description:
				'Coordinación de muestras mediante una red logística con cadena de frío para preservar su integridad.',
			icon: 'logistics'
		}
	];

	const technologies = [
		{ title: 'Secuenciación NGS', description: 'Plataformas de nueva generación para análisis genómico.' },
		{ title: 'Espectrometría LC-MS/MS', description: 'Métodos analíticos de alta sensibilidad y selectividad.' },
		{ title: 'Salas limpias', description: 'Espacios con control ambiental para procesos especializados.' },
		{ title: 'Sistemas LIMS', description: 'Gestión de información de laboratorio con medidas de seguridad y trazabilidad alineadas con 21 CFR Part 11.' }
	];

	const standards = [
		{ abbreviation: 'CLIA', title: 'Certificación', description: 'Certificación CLIA para servicios de laboratorio clínico.' },
		{ abbreviation: 'CAP', title: 'Acreditación', description: 'Acreditación del College of American Pathologists.' },
		{ abbreviation: 'GLP', title: 'Buenas prácticas', description: 'Procesos orientados a las buenas prácticas de laboratorio.' },
		{ abbreviation: 'GCP', title: 'Investigación clínica', description: 'Prácticas de calidad para investigación clínica.' }
	];
</script>

<svelte:head>
	<title>Laboratorio XYZ | Ciencia, calidad y precisión</title>
	<meta
		name="description"
		content="Conoce el Laboratorio XYZ en Austin, Texas: capacidades de bioanálisis, genómica, infraestructura avanzada y estándares de calidad para investigación."
	/>
</svelte:head>

<div class="portal-page">
	<section class="hero" aria-labelledby="hero-title">
		<div class="hero-glow hero-glow-one" aria-hidden="true"></div>
		<div class="hero-glow hero-glow-two" aria-hidden="true"></div>
		<div class="hero-inner">
			<div class="hero-copy">
				<div class="eyebrow"><span></span> Ciencia aplicada. Evidencia confiable.</div>
				<h1 id="hero-title">Impulsamos la investigación con <em>precisión</em>.</h1>
				<p class="hero-description">
					En Laboratorio XYZ combinamos experiencia científica, tecnología avanzada y procesos de calidad
					para acompañar el desarrollo de investigación clínica y bioanalítica.
				</p>
				<div class="hero-actions">
					<a href="/iniciar-sesion" class="button button-primary">
						Acceder a Zone Control
						<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 12h14m-6-6 6 6-6 6" /></svg>
					</a>
					<a href="#perfil" class="button button-quiet">Conocer el laboratorio</a>
				</div>
				<div class="hero-location">
					<svg viewBox="0 0 24 24" aria-hidden="true">
						<path d="M20 10c0 5-8 12-8 12S4 15 4 10a8 8 0 1 1 16 0Z" />
						<circle cx="12" cy="10" r="2.5" />
					</svg>
					Austin, Texas
				</div>
			</div>

			<div class="hero-visual" aria-label="Identidad del Laboratorio XYZ">
				<div class="visual-orbit orbit-outer"></div>
				<div class="visual-orbit orbit-inner"></div>
				<div class="visual-card">
					<div class="visual-logo"><img src="/logo.png" alt="Logotipo del Laboratorio XYZ" /></div>
					<div class="visual-divider"></div>
					<p class="visual-kicker">LABORATORIO XYZ</p>
					<p class="visual-title">Excelencia analítica<br />en cada resultado.</p>
					<div class="visual-tags"><span>CLIA</span><span>CAP</span><span>GLP</span><span>GCP</span></div>
				</div>
				<div class="visual-dot visual-dot-one"></div>
				<div class="visual-dot visual-dot-two"></div>
			</div>
		</div>
		<div class="hero-bottom-line"><span></span><span></span><span></span></div>
	</section>

	<section class="metrics section-wrap" aria-label="Logros del laboratorio">
		{#each metrics as metric}
			<article class="metric-card">
				<p class="metric-value">{metric.value}</p>
				<h2>{metric.label}</h2>
				<p class="metric-detail">{metric.detail}</p>
			</article>
		{/each}
	</section>

	<section class="section-wrap profile-section" id="perfil" aria-labelledby="profile-title">
		<div class="section-heading">
			<p class="eyebrow section-eyebrow">Quiénes somos</p>
			<h2 id="profile-title">Ciencia rigurosa, al servicio de mejores decisiones.</h2>
			<p>
				Ubicados en Austin, Texas, somos una instalación de alta complejidad fundada por científicos
				experimentados y comprometida con la excelencia analítica. Trabajamos con estándares exigentes
				para ofrecer resultados confiables y trazables.
			</p>
		</div>
		<div class="profile-aside">
			<div class="profile-mark" aria-hidden="true">
				<svg viewBox="0 0 64 64"><path d="M24 8h16m-13 0v17L13 48a6 6 0 0 0 5 9h28a6 6 0 0 0 5-9L37 25V8M19 43h26M25 35h14" /></svg>
			</div>
			<p class="profile-aside-title">De la muestra a la evidencia</p>
			<p class="profile-aside-copy">Procesos integrados para respaldar investigación clínica y bioanalítica.</p>
			<a href="#capacidades" class="text-link">
				Explorar capacidades
				<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 12h14m-6-6 6 6-6 6" /></svg>
			</a>
		</div>
	</section>

	<section class="technology-section" aria-labelledby="technology-title">
		<div class="section-wrap technology-layout">
			<div class="section-heading">
				<p class="eyebrow section-eyebrow">Infraestructura y tecnología</p>
				<h2 id="technology-title">Herramientas avanzadas. Procesos bajo control.</h2>
				<p>
					Integramos plataformas modernas y sistemas de gestión para respaldar flujos de trabajo
					consistentes, seguros y trazables.
				</p>
			</div>
			<div class="technology-grid">
				{#each technologies as technology, index}
					<article class="technology-card">
						<div class="technology-number">0{index + 1}</div>
						<h3>{technology.title}</h3>
						<p>{technology.description}</p>
					</article>
				{/each}
			</div>
		</div>
	</section>

	<section class="section-wrap capabilities-section" id="capacidades" aria-labelledby="capabilities-title">
		<div class="section-heading section-heading-centered">
			<p class="eyebrow section-eyebrow">Áreas de especialización</p>
			<h2 id="capabilities-title">Capacidades para acompañar cada etapa.</h2>
			<p>Experiencia multidisciplinaria para responder a las necesidades de proyectos científicos globales.</p>
		</div>
		<div class="capability-grid">
			{#each capabilities as capability}
				<article class="capability-card">
					<div class="capability-topline"><span>{capability.number}</span><span class="capability-icon">
						{#if capability.icon === 'genomics'}
							<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M7 3c10 0 10 18 0 18M17 3C7 3 7 21 17 21M8 7h8m-9 5h10m-9 5h8" /></svg>
						{:else if capability.icon === 'analysis'}
							<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 19h16M6 16l4-5 3 2 5-7m-1 0h3v3" /></svg>
						{:else}
							<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3 7h12v11H3zM15 11h4l3 3v4h-7M7 18a2 2 0 1 0 4 0m6 0a2 2 0 1 0 4 0" /></svg>
						{/if}
					</span></div>
					<h3>{capability.title}</h3>
					<p>{capability.description}</p>
				</article>
			{/each}
		</div>
	</section>

	<section class="quality-section" id="calidad" aria-labelledby="quality-title">
		<div class="section-wrap quality-layout">
			<div class="quality-intro">
				<p class="eyebrow section-eyebrow">Calidad y cumplimiento</p>
				<h2 id="quality-title">La calidad se integra en cada etapa.</h2>
				<p>
					Nuestro marco de trabajo incorpora certificaciones, acreditaciones y buenas prácticas que
					orientan la operación del laboratorio.
				</p>
			</div>
			<div class="standards-grid">
				{#each standards as standard}
					<article class="standard-card">
						<span class="standard-badge">{standard.abbreviation}</span>
						<div><h3>{standard.title}</h3><p>{standard.description}</p></div>
					</article>
				{/each}
			</div>
		</div>
	</section>

	<section class="closing-cta section-wrap" aria-labelledby="cta-title">
		<div>
			<p class="eyebrow">Zone Control</p>
			<h2 id="cta-title">Control de acceso para un entorno de confianza.</h2>
			<p>Ingresa a la plataforma para continuar con la gestión segura de las operaciones.</p>
		</div>
		<a href="/iniciar-sesion" class="button button-light">
			Iniciar sesión
			<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 12h14m-6-6 6 6-6 6" /></svg>
		</a>
	</section>

</div>

<style>
	.portal-page {
		--portal-accent: #087f9d;
		--portal-accent-strong: #06657e;
		--portal-hero: #eaf5fb;
		--portal-card: var(--lab-surface);
		--portal-ink: var(--lab-text);
		--portal-muted: var(--lab-text-muted);
		--portal-line: var(--lab-border);
		color: var(--portal-ink);
	}

	:global(:root[data-theme='dark']) .portal-page {
		--portal-accent: #55c3df;
		--portal-accent-strong: #8bd8ea;
		--portal-hero: #111f30;
	}

	.portal-page * { box-sizing: border-box; }
	.portal-page a:focus-visible { outline: 3px solid var(--portal-accent); outline-offset: 4px; }
	.section-wrap { width: min(1120px, calc(100% - 3rem)); margin-inline: auto; }

	.hero {
		position: relative;
		overflow: hidden;
		margin: 0 auto;
		border: 1px solid var(--portal-line);
		border-radius: 28px;
		background:
			radial-gradient(ellipse at 78% 50%, color-mix(in srgb, var(--portal-accent) 15%, transparent), transparent 38%),
			linear-gradient(125deg, var(--portal-hero), var(--lab-surface));
	}

	.hero-inner {
		position: relative;
		z-index: 1;
		display: grid;
		width: min(1120px, calc(100% - 3rem));
		min-height: 440px;
		grid-template-columns: minmax(0, 1.15fr) minmax(300px, 0.85fr);
		align-items: center;
		gap: 2.5rem;
		margin: auto;
		padding: 4.25rem 0;
	}

	.hero-copy { max-width: 650px; }
	.eyebrow {
		display: flex;
		align-items: center;
		gap: 0.55rem;
		margin: 0 0 1.1rem;
		color: var(--portal-accent-strong);
		font-size: 0.75rem;
		font-weight: 800;
		letter-spacing: 0.13em;
		text-transform: uppercase;
	}
	.hero .eyebrow span { width: 8px; height: 8px; border-radius: 50%; background: var(--portal-accent); box-shadow: 0 0 0 5px color-mix(in srgb, var(--portal-accent) 15%, transparent); }
	.hero h1 {
		max-width: 680px;
		margin: 0;
		color: var(--portal-ink);
		font-size: clamp(2.65rem, 5.3vw, 4.35rem);
		font-weight: 800;
		letter-spacing: -0.055em;
		line-height: 1.04;
	}
	.hero h1 em { color: var(--portal-accent-strong); font-style: normal; }
	.hero-description { max-width: 590px; margin: 1.25rem 0 1.8rem; color: var(--portal-muted); font-size: 1.06rem; line-height: 1.75; }
	.hero-actions { display: flex; flex-wrap: wrap; align-items: center; gap: 0.8rem; }
	.button {
		display: inline-flex;
		min-height: 48px;
		align-items: center;
		justify-content: center;
		gap: 0.7rem;
		padding: 0.75rem 1.1rem;
		border: 1px solid transparent;
		border-radius: 12px;
		font-size: 0.92rem;
		font-weight: 700;
		text-decoration: none;
		transition: transform 160ms ease, background-color 160ms ease, border-color 160ms ease;
	}
	.button:hover { transform: translateY(-2px); }
	.button svg, .text-link svg { width: 18px; height: 18px; fill: none; stroke: currentColor; stroke-width: 1.8; stroke-linecap: round; stroke-linejoin: round; }
	.button-primary { background: #087f9d; color: #fff; }
	.button-primary:hover { background: #06657e; }
	.button-quiet { border-color: var(--portal-line); background: var(--portal-card); color: var(--portal-ink); }
	.button-quiet:hover { border-color: var(--portal-accent); }
	.hero-location { display: flex; align-items: center; gap: 0.45rem; margin-top: 1.55rem; color: var(--portal-muted); font-size: 0.9rem; }
	.hero-location svg { width: 17px; height: 17px; fill: none; stroke: var(--portal-accent-strong); stroke-width: 1.7; }

	.hero-visual { position: relative; display: grid; min-height: 310px; place-items: center; }
	.visual-card {
		position: relative;
		z-index: 1;
		width: min(310px, 90%);
		padding: 2rem 1.8rem 1.7rem;
		border: 1px solid color-mix(in srgb, var(--portal-accent) 25%, var(--portal-line));
		border-radius: 22px;
		background: color-mix(in srgb, var(--portal-card) 92%, transparent);
		box-shadow: 0 24px 70px rgb(15 38 55 / 14%);
		text-align: center;
		backdrop-filter: blur(14px);
	}
	.visual-logo { display: grid; width: 82px; height: 82px; place-items: center; margin: 0 auto 1.1rem; border: 1px solid var(--portal-line); border-radius: 22px; background: #fff; }
	.visual-logo img { width: 68px; height: 68px; object-fit: contain; }
	.visual-divider { width: 38px; height: 2px; margin: 0 auto 1.1rem; background: var(--portal-accent); }
	.visual-kicker { margin: 0; color: var(--portal-accent-strong); font-size: 0.67rem; font-weight: 800; letter-spacing: 0.18em; }
	.visual-title { margin: 0.55rem 0 1.15rem; color: var(--portal-ink); font-size: 1.23rem; font-weight: 750; line-height: 1.4; }
	.visual-tags { display: flex; justify-content: center; gap: 0.4rem; }
	.visual-tags span { padding: 0.3rem 0.48rem; border: 1px solid var(--portal-line); border-radius: 999px; color: var(--portal-muted); font-size: 0.65rem; font-weight: 750; }
	.visual-orbit { position: absolute; border: 1px solid color-mix(in srgb, var(--portal-accent) 28%, transparent); border-radius: 50%; }
	.orbit-outer { width: 340px; height: 340px; transform: rotate(-28deg) scaleY(0.55); }
	.orbit-inner { width: 275px; height: 275px; transform: rotate(28deg) scaleY(0.58); }
	.visual-dot { position: absolute; z-index: 2; width: 12px; height: 12px; border: 3px solid var(--portal-card); border-radius: 50%; background: var(--portal-accent); box-shadow: 0 0 0 5px color-mix(in srgb, var(--portal-accent) 15%, transparent); }
	.visual-dot-one { top: 18%; right: 18%; }
	.visual-dot-two { bottom: 18%; left: 13%; width: 9px; height: 9px; }
	.hero-glow { position: absolute; width: 280px; height: 280px; border-radius: 50%; background: color-mix(in srgb, var(--portal-accent) 10%, transparent); filter: blur(70px); }
	.hero-glow-one { top: -180px; right: 15%; }
	.hero-glow-two { bottom: -230px; left: 25%; }
	.hero-bottom-line { display: flex; height: 4px; gap: 0.3rem; }
	.hero-bottom-line span:first-child { width: 18%; background: var(--portal-accent); }
	.hero-bottom-line span:nth-child(2) { width: 8%; background: color-mix(in srgb, var(--portal-accent) 45%, transparent); }
	.hero-bottom-line span:last-child { flex: 1; background: color-mix(in srgb, var(--portal-accent) 14%, transparent); }

	.metrics { display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem; margin-top: 1.5rem; }
	.metric-card { min-height: 150px; padding: 1.45rem 1.6rem; border: 1px solid var(--portal-line); border-radius: 16px; background: var(--portal-card); box-shadow: var(--lab-shadow); }
	.metric-value { margin: 0 0 0.4rem; color: var(--portal-accent-strong); font-size: 2rem; font-weight: 850; letter-spacing: -0.045em; }
	.metric-card h2 { margin: 0; color: var(--portal-ink); font-size: 0.95rem; font-weight: 750; }
	.metric-detail { margin: 0.25rem 0 0; color: var(--portal-muted); font-size: 0.85rem; }

	.profile-section { display: grid; grid-template-columns: 1.15fr 0.85fr; align-items: center; gap: 5rem; padding-block: 6.5rem; }
	.section-heading { max-width: 650px; }
	.section-eyebrow { margin-bottom: 0.7rem; }
	.section-heading h2, .quality-intro h2 {
		margin: 0;
		color: var(--portal-ink);
		font-size: clamp(1.8rem, 3.4vw, 2.65rem);
		font-weight: 800;
		letter-spacing: -0.045em;
		line-height: 1.15;
	}
	.section-heading > p:not(.eyebrow), .quality-intro > p:not(.eyebrow) { margin: 1rem 0 0; color: var(--portal-muted); font-size: 1rem; line-height: 1.8; }
	.profile-aside { position: relative; padding: 2rem; border: 1px solid var(--portal-line); border-radius: 20px; background: var(--portal-card); box-shadow: var(--lab-shadow); }
	.profile-mark { display: grid; width: 54px; height: 54px; place-items: center; border-radius: 16px; background: color-mix(in srgb, var(--portal-accent) 12%, var(--portal-card)); color: var(--portal-accent-strong); }
	.profile-mark svg { width: 32px; height: 32px; fill: none; stroke: currentColor; stroke-width: 1.5; stroke-linecap: round; stroke-linejoin: round; }
	.profile-aside-title { margin: 1.2rem 0 0.4rem; color: var(--portal-ink); font-size: 1.15rem; font-weight: 750; }
	.profile-aside-copy { margin: 0; color: var(--portal-muted); line-height: 1.65; }
	.text-link { display: inline-flex; align-items: center; gap: 0.45rem; margin-top: 1.25rem; color: var(--portal-accent-strong); font-size: 0.9rem; font-weight: 750; text-decoration: none; }

	.technology-section { padding-block: 5.5rem; background: color-mix(in srgb, var(--lab-surface-raised) 75%, var(--lab-bg)); }
	.technology-layout { display: grid; grid-template-columns: 0.8fr 1.2fr; gap: 4rem; align-items: start; }
	.technology-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 0.85rem; }
	.technology-card { min-height: 170px; padding: 1.3rem; border: 1px solid var(--portal-line); border-radius: 15px; background: var(--portal-card); }
	.technology-number { margin-bottom: 1rem; color: var(--portal-accent-strong); font-size: 0.72rem; font-weight: 850; letter-spacing: 0.12em; }
	.technology-card h3 { margin: 0 0 0.45rem; color: var(--portal-ink); font-size: 1rem; font-weight: 750; }
	.technology-card p { margin: 0; color: var(--portal-muted); font-size: 0.88rem; line-height: 1.65; }

	.capabilities-section { padding-block: 6rem; }
	.section-heading-centered { max-width: 710px; margin: 0 auto 2.3rem; text-align: center; }
	.section-heading-centered .eyebrow { justify-content: center; }
	.capability-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem; }
	.capability-card { min-height: 260px; padding: 1.6rem; border: 1px solid var(--portal-line); border-radius: 17px; background: var(--portal-card); box-shadow: var(--lab-shadow); transition: transform 180ms ease, border-color 180ms ease; }
	.capability-card:hover { transform: translateY(-4px); border-color: color-mix(in srgb, var(--portal-accent) 45%, var(--portal-line)); }
	.capability-topline { display: flex; align-items: center; justify-content: space-between; margin-bottom: 2rem; color: var(--portal-accent-strong); font-size: 0.75rem; font-weight: 800; letter-spacing: 0.12em; }
	.capability-icon { display: grid; width: 42px; height: 42px; place-items: center; border-radius: 13px; background: color-mix(in srgb, var(--portal-accent) 11%, var(--portal-card)); }
	.capability-icon svg { width: 23px; height: 23px; fill: none; stroke: currentColor; stroke-width: 1.6; stroke-linecap: round; stroke-linejoin: round; }
	.capability-card h3 { margin: 0 0 0.65rem; color: var(--portal-ink); font-size: 1.08rem; line-height: 1.4; }
	.capability-card p { margin: 0; color: var(--portal-muted); font-size: 0.9rem; line-height: 1.7; }

	.quality-section { padding-block: 5.5rem; background: color-mix(in srgb, var(--lab-surface-raised) 75%, var(--lab-bg)); }
	.quality-layout { display: grid; grid-template-columns: 0.75fr 1.25fr; align-items: center; gap: 4rem; }
	.standards-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 0.8rem; }
	.standard-card { display: flex; min-height: 112px; align-items: flex-start; gap: 0.9rem; padding: 1rem; border: 1px solid var(--portal-line); border-radius: 14px; background: var(--portal-card); }
	.standard-badge { display: grid; min-width: 48px; height: 42px; place-items: center; border-radius: 10px; background: color-mix(in srgb, var(--portal-accent) 12%, var(--portal-card)); color: var(--portal-accent-strong); font-size: 0.68rem; font-weight: 850; }
	.standard-card h3 { margin: 0.1rem 0 0.3rem; color: var(--portal-ink); font-size: 0.9rem; }
	.standard-card p { margin: 0; color: var(--portal-muted); font-size: 0.78rem; line-height: 1.55; }

	.closing-cta { display: flex; align-items: center; justify-content: space-between; gap: 2rem; margin-block: 5rem; padding: 2.3rem 2.5rem; border-radius: 20px; background: linear-gradient(120deg, #075b74, #087f9d); color: #fff; }
	.closing-cta .eyebrow { margin-bottom: 0.45rem; color: #b7ecf6; }
	.closing-cta h2 { max-width: 650px; margin: 0; color: #fff; font-size: clamp(1.4rem, 3vw, 2rem); letter-spacing: -0.035em; }
	.closing-cta > div > p:last-child { margin: 0.6rem 0 0; color: #d4f1f7; font-size: 0.92rem; }
	.button-light { flex: 0 0 auto; background: #fff; color: #064c61; }
	.button-light:hover { background: #e8f7fa; }

	

	@media (max-width: 850px) {
		.hero-inner { grid-template-columns: 1fr 0.75fr; gap: 1rem; }
		.profile-section, .technology-layout, .quality-layout { gap: 2.2rem; }
		
	}

	@media (max-width: 650px) {
		.section-wrap, .hero-inner { width: min(100% - 2rem, 540px); }
		.hero { border-radius: 20px; }
		.hero-inner { grid-template-columns: 1fr; padding-block: 3rem 2rem; }
		.hero h1 { font-size: clamp(2.45rem, 12vw, 3.5rem); }
		.hero-description { font-size: 0.98rem; }
		.hero-visual { min-height: 275px; }
		.visual-card { width: min(290px, 88%); }
		.metrics, .capability-grid { grid-template-columns: 1fr; }
		.metric-card { min-height: auto; }
		.profile-section, .technology-layout, .quality-layout { grid-template-columns: 1fr; padding-block: 4rem; }
		.technology-section, .quality-section { padding-block: 4rem; }
		.capabilities-section { padding-block: 4rem; }
		.technology-grid, .standards-grid { grid-template-columns: 1fr; }
		.capability-card { min-height: auto; }
		.capability-topline { margin-bottom: 1.4rem; }
		.closing-cta { align-items: flex-start; flex-direction: column; width: calc(100% - 2rem); margin-block: 3rem; padding: 1.6rem; }
		
	}

	@media (prefers-reduced-motion: reduce) {
		.portal-page *, .portal-page *::before, .portal-page *::after { scroll-behavior: auto !important; transition-duration: 0.01ms !important; }
	}
</style>
