{{- define "cas.name" -}}
{{- .Chart.Name }}
{{- end }}

{{- define "cas.fullname" -}}
{{- printf "%s-%s" .Release.Name .Chart.Name }}
{{- end }}

{{- define "cas.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version }}
{{- end }}

{{- define "cas.labels" -}}
helm.sh/chart: {{ include "cas.chart" . }}
{{ include "cas.selectorLabels" . }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{- define "cas.selectorLabels" -}}
app.kubernetes.io/name: {{ include "cas.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}
